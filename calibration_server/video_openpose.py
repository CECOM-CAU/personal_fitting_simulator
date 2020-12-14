import cv2
import numpy as np
from keras.models import load_model
import test

points = []
delta = {}
people_height = 0
def video_output_keypoints(frame, net, threshold, BODY_PARTS, now_frame, total_frame, drawn):
    global points
    # 입력 이미지의 사이즈 정의
    image_height = 368
    image_width = 368

    # 네트워크에 넣기 위한 전처리
    input_blob = cv2.dnn.blobFromImage(frame, 1.0 / 255, (image_width, image_height), (0, 0, 0), swapRB=False,
                                       crop=False)
    # 전처리된 blob 네트워크에 입력
    net.setInput(input_blob)

    # 결과 받아오기
    out = net.forward()
    out_height = out.shape[2]
    out_width = out.shape[3]

    # 원본 이미지의 높이, 너비를 받아오기
    frame_height, frame_width = frame.shape[:2]

    # 포인트 리스트 초기화
    points = []

    print(f"============================== frame: {now_frame:.0f} / {total_frame:.0f} ==============================")
    for i in range(len(BODY_PARTS)):
        # 신체 부위의 confidence map
        prob_map = out[0, i, :, :]
        # 최소값, 최대값, 최소값 위치, 최대값 위치
        min_val, prob, min_loc, point = cv2.minMaxLoc(prob_map)
        # 원본 이미지에 맞게 포인트 위치 조정
        x = (frame_width * point[0]) / out_width
        x = int(x)
        y = (frame_height * point[1]) / out_height
        y = int(y)
        if prob > threshold:  # [pointed]
            cv2.circle(drawn, (x, y), 5, (0, 255, 255), thickness=-1, lineType=cv2.FILLED)
            cv2.putText(drawn, str(i), (x, y), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 0, 255), 1, lineType=cv2.LINE_AA)
            points.append((x, y))
            #print(f"[pointed] {BODY_PARTS[i]} ({i}) => prob: {prob:.5f} / x: {x} / y: {y}")

        else:  # [not pointed]
            cv2.circle(drawn, (x, y), 5, (0, 255, 255), thickness=-1, lineType=cv2.FILLED)
            cv2.putText(drawn, str(i), (x, y), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 0, 0), 1, lineType=cv2.LINE_AA)
            points.append(None)
            #print(f"[not pointed] {BODY_PARTS[i]} ({i}) => prob: {prob:.5f} / x: {x} / y: {y}")

    return drawn

def video_output_keypoints_with_lines(frame, POSE_PAIRS):
    global delta
    for pair in POSE_PAIRS:
        part_a = pair[0]  # 0 (Head)
        part_b = pair[1]  # 1 (Neck)
        if points[part_a] and points[part_b]:
            delta[(part_a, part_b)] = -1 * (points[part_a][0] - points[part_b][0]) / (points[part_a][1] - points[part_b][1] + 0.00001)
            delta[(part_b, part_a)] = -1 * (points[part_a][0] - points[part_b][0]) / (points[part_a][1] - points[part_b][1] + 0.00001)

            #print(f"[linked] {part_a} {points[part_a]} <=> {part_b} {points[part_b]}")
            #cv2.line(frame, points[part_a], points[part_b], (0, 255, 0), 2)
        #else:
            #print(f"[not linked] {part_a} {points[part_a]} <=> {part_b} {points[part_b]}")

    return frame

def output_keypoints_with_lines_video(proto_file, weights_file, video_path, threshold, BODY_PARTS, POSE_PAIRS):
    global points
    global people_height
    data = []
    # 네트워크 불러오기
    net = cv2.dnn.readNetFromCaffe(proto_file, weights_file)

    # GPU 사용
    net.setPreferableBackend(cv2.dnn.DNN_BACKEND_CUDA)
    net.setPreferableTarget(cv2.dnn.DNN_TARGET_CUDA)

    # 비디오 읽어오기
    capture = cv2.VideoCapture(video_path)
    now_frame_boy = capture.get(cv2.CAP_PROP_POS_FRAMES)
    total_frame_boy = capture.get(cv2.CAP_PROP_FRAME_COUNT)

    count_45 = 0
    count_90 = 0
    count_180 = 0
    count_360 = 0

    available_45 = False
    available_90 = False
    available_180 = True
    available_360 = True
    image_list = []
    while True:
        now_frame_boy = capture.get(cv2.CAP_PROP_POS_FRAMES)
        total_frame_boy = capture.get(cv2.CAP_PROP_FRAME_COUNT)

        if now_frame_boy == total_frame_boy:
            break

        ret, frame_boy = capture.read()
        frame_boy = Rotate(frame_boy,90)
        temp_height,temp_width,temp_ND = frame_boy.shape
        org_frame_boy = cv2.resize(frame_boy, dsize=(int(temp_width), int(temp_height)))
        frame_boy = org_frame_boy.copy()
        seg_frame = org_frame_boy.copy()
        frame_boy = video_output_keypoints(frame=frame_boy, net=net, threshold=threshold, BODY_PARTS=BODY_PARTS,
                                     now_frame=now_frame_boy, total_frame=total_frame_boy,drawn=seg_frame)
        frame_boy = video_output_keypoints_with_lines(frame=frame_boy, POSE_PAIRS=POSE_PAIRS)


        stop_length = np.sqrt((points[6][0] - points[5][0]) ** 2 + ((points[6][1] - points[5][1]) ** 2))
        """if abs(points[6][1]-points[5][1]) < 30:
            stop_length = np.sqrt((points[6][0]-points[5][0])**2 + ((points[6][1]-points[5][1])**2))
        else:
            stop_length = -1
        """
        image_list.append([stop_length, org_frame_boy, points[:]])
        print(stop_length)
        #cv2.imshow("Output_Keypoints", frame_boy)

        #if cv2.waitKey(10) == 27:  # esc 입력시 종료
            #break

    image_list.sort(key=lambda x:x[0])

    """
    temp_length = []
    for x in image_list:
        temp_length.append(x[0])
    temp_length.sort()
    set_temp = set(temp_length[0:-3])
    """

    standard_length =image_list[-3][0]
    print(standard_length)
    index = 0
    seg_img = test.getSegImage(image_list[1][1])
    org_img = image_list[1][1].copy()

    image_list[1][1] = video_output_keypoints(frame=org_img, net=net, threshold=threshold, BODY_PARTS=BODY_PARTS,
                                  now_frame=now_frame_boy, total_frame=total_frame_boy, drawn=seg_img)
    image_list[1][1]= video_output_keypoints_with_lines(frame=seg_img, POSE_PAIRS=POSE_PAIRS)
    cv2.imwrite('test_image/test_90.jpg', image_list[1][1])
    cv2.imwrite('test_image/test_90_org.jpg', org_img)
    print("90")
    returnLength = getSideSize(image_list[1][1], peopleLength=people_height, points=image_list[1][2])
    data.append(returnLength)

    for x in range(len(image_list)-1,0,-1):
        if abs(image_list[x][2][5][1]-image_list[x][2][6][1]) == 0:
            seg_img = test.getSegImage(image_list[x][1])
            org_img = image_list[x][1].copy()
            image_list[x][1] = video_output_keypoints(frame=org_img, net=net, threshold=threshold, BODY_PARTS=BODY_PARTS,
                                                   now_frame=now_frame_boy, total_frame=total_frame_boy, drawn=seg_img)
            image_list[x][1] = video_output_keypoints_with_lines(frame=seg_img, POSE_PAIRS=POSE_PAIRS)
            cv2.imwrite('test_image/test_360.jpg', image_list[x][1])
            cv2.imwrite('test_image/test_360_org.jpg', org_img)
            returnLength = getFrontSize(image_list[x][1], peopleLength=people_height, points=image_list[x][2])
            data.append(returnLength)
            print("360")
            break
    count_45 = 1
    """
    for x in image_list:
        print("frame:" +str(index))
        index += 1
        detect_count = 0
        if x[0] < 0:
            continue
        if (abs(standard_length / np.sqrt(2) - x[0]) >= 35):
            available_45 = True
        if (abs(standard_length / np.sqrt(2) - x[0]) < 35 and available_45 == True):
            available_45 = False
            detect_count = 1
            seg_img = test.getSegImage(x[1])
            org_img = x[1].copy()

            x[1] = video_output_keypoints(frame=org_img, net=net, threshold=threshold, BODY_PARTS=BODY_PARTS,
                                               now_frame=now_frame_boy, total_frame=total_frame_boy, drawn=seg_img)
            x[1] = video_output_keypoints_with_lines(frame=seg_img, POSE_PAIRS=POSE_PAIRS)
            if count_45 == 0 :
                cv2.imwrite('test_image/test_45.jpg', x[1])
                cv2.imwrite('test_image/test_45_org.jpg', org_img)
                print("45")
                count_45 = 1
            elif count_45 == 1 and count_90 == 1:
                cv2.imwrite('test_image/test_135.jpg', x[1])
                cv2.imwrite('test_image/test_135_org.jpg', org_img)
                print("135")
                count_45 = 2
            elif count_45 == 2 and count_180 == 1:
                cv2.imwrite('test_image/test_225.jpg', x[1])
                cv2.imwrite('test_image/test_225_org.jpg', org_img)
                print("225")
                count_45 = 3
            elif count_45 == 3 and count_90 == 2:
                cv2.imwrite('test_image/test_315.jpg', x[1])
                cv2.imwrite('test_image/test_315_org.jpg', org_img)
                print("315")
                count_45 = 4

        if x[0] >= 35 :
            available_90 = True
        elif  x[0] <= 35 and available_90 == True:
            detect_count = 1
            seg_img = test.getSegImage(x[1])
            org_img = x[1].copy()
            x[1] = video_output_keypoints(frame=org_img, net=net, threshold=threshold, BODY_PARTS=BODY_PARTS,
                                          now_frame=now_frame_boy, total_frame=total_frame_boy, drawn=seg_img)
            x[1] = video_output_keypoints_with_lines(frame=seg_img, POSE_PAIRS=POSE_PAIRS)
            available_90 = False
            if count_90 == 0:
                cv2.imwrite('test_image/test_90.jpg', x[1])
                cv2.imwrite('test_image/test_90_org.jpg', org_img)
                returnLength = getSideSize(x[1], points=x[2])
                data.append(returnLength)
                print("90")
                count_90 = 1
            elif count_90 == 1 and count_45 == 3:
                cv2.imwrite('test_image/test_270.jpg', x[1])
                cv2.imwrite('test_image/test_270_org.jpg', org_img)
                print("270")
                count_90 = 2


        if  abs(standard_length-x[0]) < 35 and available_180 == True and count_45 == 2:
            detect_count = 1
            seg_img = test.getSegImage(x[1])
            org_img = x[1].copy()
            x[1] = video_output_keypoints(frame=org_img, net=net, threshold=threshold, BODY_PARTS=BODY_PARTS,
                                          now_frame=now_frame_boy, total_frame=total_frame_boy, drawn=seg_img)
            x[1] = video_output_keypoints_with_lines(frame=seg_img, POSE_PAIRS=POSE_PAIRS)
            available_180 = False
            cv2.imwrite('test_image/test_180.jpg', x[1])
            cv2.imwrite('test_image/test_180_org.jpg', org_img)
            print("180")
            count_180 = 1


        if  abs(standard_length-x[0]) < 20 and available_360 == True and count_45 == 4 and abs(x[2][12][1] - x[2][11][1]) < 10:
            detect_count = 1
            seg_img = test.getSegImage(x[1])
            org_img = x[1].copy()
            x[1] = video_output_keypoints(frame=org_img, net=net, threshold=threshold, BODY_PARTS=BODY_PARTS,
                                          now_frame=now_frame_boy, total_frame=total_frame_boy, drawn=seg_img)
            x[1] = video_output_keypoints_with_lines(frame=seg_img, POSE_PAIRS=POSE_PAIRS)
            available_360 = False
            cv2.imwrite('test_image/test_360.jpg', x[1])
            cv2.imwrite('test_image/test_360_org.jpg', org_img)
            returnLength = getFrontSize(x[1], points=x[2])
            data.append(returnLength)
            print("360")
            count_360 = 1
    """
    capture.release()
    cv2.destroyAllWindows()
    print('height:'+str(people_height))
    return data
def Rotate(src, degrees):
    if degrees == 90:
        dst = cv2.transpose(src)
        dst = cv2.flip(dst, 1)

    elif degrees == 180:
        dst = cv2.flip(src, -1)

    elif degrees == 270:
        dst = cv2.transpose(src)
        dst = cv2.flip(dst, 0)
    else:
        dst = null
    return dst

def getFrontSize(frame_drawn,peopleLength=1630,points = []):
    returnList = [0 for x in range(10)]
    length = ((points[24][1] - points[18][1]) + (points[21][1] - points[18][1])) / 2
    pixelPerMM = peopleLength / length

    delta_10 = abs((points[12][1] + points[11][1]) / 2 - points[17][1]) / 100

    # body_delta = getDelta(points[12],points[11]) +0.00001
    bodyLength = 0

    prev_bodyLength = 0

    chestToShoulderLength = 0

    neck_to_shoulder = 0
    shoulder_length = 0
    arm_length = 0
    arm_long_length = 0
    armpit_length = 0
    shoulder_to_waist_length = 0
    stomach_length = 0
    chest_length = 0

    chest_to_body_length = 0

    shoulder_y = 0
    y_count = 0
    MAX_point = 0
    for y in range(1, 100):
        if y == 40:
            stomach_length = MAX_point
            MAX_point = 0
        x_body = (points[12][0] + points[11][0]) // 2
        prev_y = points[12][1] - int(delta_10 * (y - 1))
        prev_x = x_body
        delta_x = 5
        y_body = points[12][1] - int(delta_10 * (y))
        while True:
            prev_y = y_body
            prev_x = x_body
            x_body -= delta_x
            color = frame_drawn[y_body, x_body]
            if np.array_equal(frame_drawn[y_body, x_body - 10], np.array([0, 0, 0])):
                point1 = [x_body, y_body]
                break
            cv2.line(frame_drawn, (prev_x, prev_y), (x_body, y_body), (0, 0, 255), 2)
        if (x_body > 3000):
            break
        x_body = (points[12][0] + points[11][0]) // 2
        prev_y = points[12][1] - int(delta_10 * (y - 1))
        prev_x = (points[12][0] + points[11][0]) // 2

        delta_x = -5
        while True:
            prev_y = y_body
            prev_x = x_body
            x_body -= delta_x

            if np.array_equal(frame_drawn[y_body, x_body + 10], np.array([0, 0, 0])):
                point2 = [x_body, y_body]
                break
            cv2.line(frame_drawn, (prev_x, prev_y), (x_body, y_body), (0, 0, 255), 2)
        tempLength = np.sqrt((point1[0] - point2[0]) ** 2 + (point1[1] - point2[1]) ** 2)
        if(tempLength > MAX_point):
            MAX_point = tempLength
        print("length(" + str(point1[0]) + "),(" + str(point2[0]) + ")=" + str(tempLength))
        if prev_bodyLength == 0:
            prev_bodyLength = tempLength
        if y == 5:
            bodyLength = tempLength
        if (abs(tempLength - prev_bodyLength) > 35):
            armpit_length = prev_bodyLength
            chest_to_body_length = abs(points[11][1] - y_body)
            chestToShoulderLength = abs(points[5][1] - y_body)
            chest_length = MAX_point
            y_count = y
            break
        else:
            # bodyLength += tempLength
            prev_bodyLength = tempLength

    prev_bodyLength = 0
    x_body = points[5][0]
    y_body = max([points[5][1],points[6][1]])
    y_body += 3
    prev_x = x_body - 3
    while True:
        y_body -= 3
        x_body = points[5][0]
        delta_x = 3
        while True:
            x_body += delta_x
            if np.array_equal(frame_drawn[y_body, x_body+5], np.array([0, 0, 0])):
                point1 = [x_body, y_body]
                break
            cv2.line(frame_drawn, (prev_x, y_body), (x_body, y_body), (255, 0, 0), 1)
            prev_x = x_body
            if (x_body > 3000):
                break

        x_body = points[6][0]
        prev_x = x_body + 3
        delta_x = 3
        while True:
            x_body -= delta_x
            if np.array_equal(frame_drawn[y_body, x_body-5], np.array([0, 0, 0])):
                point2 = [x_body, y_body]
                break
            cv2.line(frame_drawn, (prev_x, y_body), (x_body, y_body), (255, 0, 0), 1)
            prev_x = x_body
            if (x_body <= 0):
                break
        tempLength = np.sqrt((point1[0] - point2[0]) ** 2 + (point1[1] - point2[1]) ** 2)
        if prev_bodyLength == 0:
            prev_bodyLength = tempLength
            shoulder_length = tempLength
            shoulder_y = y_body
            break
        elif (tempLength - prev_bodyLength > 35):
            #armLength = prev_bodyLength
            tempLength = prev_bodyLength
            shoulder_length = tempLength
            shoulder_y = y_body
            break


    #shoulder_Length = np.sqrt((point1[0] - point2[0]) ** 2 + (point1[1] - point2[1]) ** 2)
    #print("length(" + str(point1[0]) + "),(" + str(point2[0]) + ")=" + str(tempLength))

    cv2.line(frame_drawn, (points[10][0], points[10][1]), (points[6][0], points[6][1]), (0, 255, 0), 2)
    cv2.line(frame_drawn, (points[9][0], points[9][1]), (points[5][0], points[5][1]), (0, 255, 0), 2)
    arm_long_length = (np.sqrt((points[10][0] - points[6][0]) ** 2 + (points[10][1] - points[6][1]) ** 2) +np.sqrt((points[5][0] - points[9][0]) ** 2 + (points[5][1] - points[9][1]) ** 2))/2
    neck_to_shoulder = abs(shoulder_y - points[17][1])
    shoulder_to_waist_length = abs(shoulder_y-points[11][1])
    start_point = 7
    end_point = 5
    delta_100 = (points[end_point][0] - points[start_point][0]) / 100
    prev_bodyLength = 0
    x_count = 0
    for x in range(1, 100):

        x_count += 1
        prev_x = points[start_point][0] + int(delta_100 * (x - 1))
        prev_y = points[start_point][1] + int(delta_100 * (x - 1) * -1 / delta[(start_point, end_point)])
        delta_x = 5
        x_7 = points[start_point][0] + int(delta_100 * (x))
        y_7 = points[start_point][1] + int(delta_100 * (x) * -1 / delta[(start_point, end_point)])
        org_x_7 = x_7
        org_y_7 = y_7
        while True:
            prev_x = x_7
            prev_y = y_7
            x_7 += delta_x
            y_7 += int(delta[(start_point, end_point)] * delta_x)
            color = frame_drawn[y_7, x_7]
            print("color(" + str(x_7) + "),(" + str(y_7) + ")=" + str(color) + str(type(color)))
            if np.array_equal(frame_drawn[y_7, x_7], np.array([0, 0, 0])):
                point1 = [x_7, y_7]
                break
            cv2.line(frame_drawn, (prev_x, prev_y), (x_7, y_7), (0, 255, 0), 1)
        delta_x = -5
        #x_7 =  org_x_7 -  int(delta_100 * (x))
        #y_7 =  org_y_7 - int(delta_100 * (x) * -1 / delta[(start_point, end_point)])
        while True:
            prev_x = x_7
            prev_y = y_7
            x_7 += delta_x
            y_7 += int(delta[(start_point, end_point)] * delta_x)
            color = frame_drawn[y_7, x_7]
            #print("color(" + str(x_7) + "),(" + str(y_7) + ")=" + str(color) + str(type(color)))
            if np.array_equal(frame_drawn[y_7, x_7], np.array([0, 0, 0])):
                point2 = [x_7, y_7]
                break
            cv2.line(frame_drawn, (prev_x, prev_y), (x_7, y_7), (0, 255, 0), 1)
        tempLength = np.sqrt((point1[0] - point2[0]) ** 2 + (point1[1] - point2[1]) ** 2)
        print("tempLength:"+str(tempLength))
        if prev_bodyLength == 0:
            prev_bodyLength = tempLength
        elif (tempLength - prev_bodyLength > 45):
            #armLength = prev_bodyLength
            tempLength = prev_bodyLength
            arm_length += tempLength
            break
        arm_length += np.sqrt((point1[0] - point2[0]) ** 2 + (point1[1] - point2[1]) ** 2)
        prev_bodyLength = tempLength
    arm_length /= x_count


    print("waist length:" + str(neck_to_shoulder * pixelPerMM))
    print("armpit length:" + str(shoulder_length * pixelPerMM))
    print("armpit To waist length:" + str(arm_length * pixelPerMM))
    print("armpitToShoulder length:" + str(arm_long_length * pixelPerMM))
    print("arm_long_length: " + str(armpit_length  * pixelPerMM))
    print("shoulder_length: " + str(shoulder_to_waist_length * pixelPerMM))
    #print("x_count:"+str(x_count)+"  arm length:" + str(arm_length * pixelPerMM))

    returnList[0] = (neck_to_shoulder * pixelPerMM)
    returnList[1] = (shoulder_length * pixelPerMM)
    returnList[2] = arm_length * pixelPerMM
    returnList[3] = arm_long_length * pixelPerMM
    returnList[4] = armpit_length * pixelPerMM
    returnList[5] = shoulder_to_waist_length * pixelPerMM
    returnList[6] = stomach_length * pixelPerMM
    returnList[7] = chest_length * pixelPerMM
    cv2.imwrite("test_image/front.jpg", frame_drawn)

    return returnList
def getSideSize(frame_drawn,peopleLength=1630,points = []):
    returnList = [0 for x in range(10)]
    length = ((points[24][1] - points[18][1]) + (points[21][1] - points[18][1])) / 2
    pixelPerMM = int(peopleLength) / length

    delta_10 = abs((points[12][1] + points[11][1]) / 2 - points[17][1]) / 100

    # body_delta = getDelta(points[12],points[11]) +0.00001
    stomach_length = 0
    chest_length = 0

    prev_bodyLength = 0
    chest_to_waist_length = 0
    stomach_to_waist_length = 0
    y_count = 0
    MAX_point = 0
    may_y = 0
    for y in range(1, 100):
        if y == 50:
            stomach_length = MAX_point
            MAX_point = 0
            stomach_to_waist_length = abs(y_body-points[12][1])
            stomach_y = y_body
        x_body = (points[12][0] + points[11][0]) // 2
        prev_y = points[12][1] - int(delta_10 * (y - 1))
        prev_x = x_body
        delta_x = 10
        y_body = points[12][1] - int(delta_10 * (y))
        while True:
            cv2.line(frame_drawn, (prev_x, prev_y), (x_body, y_body), (0, 255, 0), 2)
            prev_y = y_body
            prev_x = x_body

            x_body -= delta_x
            color = frame_drawn[y_body, x_body]
            if np.array_equal(frame_drawn[y_body, x_body - 10], np.array([0, 0, 0])):
                point1 = [x_body, y_body]
                break
        if (x_body > 3000):
            break
        x_body = (points[12][0] + points[11][0]) // 2
        prev_y = points[12][1] - int(delta_10 * (y - 1))
        prev_x = (points[12][0] + points[11][0]) // 2

        delta_x = -10
        while True:
            cv2.line(frame_drawn, (prev_x, prev_y), (x_body, y_body), (0, 255, 0), 2)
            prev_y = y_body
            prev_x = x_body
            x_body -= delta_x
            color = frame_drawn[y_body, x_body]
            # print("color("+str(x_body) + "),("+str(y_body)+")=" + str(color) + str(type(color)))
            if np.array_equal(frame_drawn[y_body, x_body + 10], np.array([0, 0, 0])):
                point2 = [x_body, y_body]
                break
        tempLength = np.sqrt((point1[0] - point2[0]) ** 2 + (point1[1] - point2[1]) ** 2)
        print("length(" + str(point1[0]) + "),(" + str(point2[0]) + ")=" + str(tempLength))
        if MAX_point < tempLength:
            MAX_point = tempLength
            chest_to_waist_length = abs(y_body- points[12][1])
        else:
            # bodyLength += tempLength
            prev_bodyLength = tempLength

    chest_length = MAX_point
    #chest_to_waist_length = abs(max_y - points[12][1])
    print("chestToBody length:" + str(chest_length * pixelPerMM))
    print("chestToShoulder length:" + str(stomach_length * pixelPerMM))
    start_point = 7
    end_point = 9

    returnList[1] = chest_length * pixelPerMM
    returnList[0] = stomach_length * pixelPerMM
    returnList[2] = chest_to_waist_length * pixelPerMM
    returnList[3] = stomach_to_waist_length * pixelPerMM
    cv2.imwrite("test_image/side.jpg", frame_drawn)


    return returnList
def getModelData(f,height):
    global people_height
    people_height= int(height)
    BODY_PARTS_BODY_25B = {0: "Nose", 1: "LEye", 2: "REye", 3: "LEar", 4: "REar", 5: "LShoulder", 6: "RSoulder",
                               7: "LElbow", 8: "RElbow", 9: "LWrist", 10: "RWrist", 11: "LHip", 12: "RHip", 13: "LKnee",
                               14: "RKnee", 15: "LAnkle", 16: "RAnkle", 17: "Neck", 18: "Head", 19: "LBigToe",
                               20: "LSmallToe", 21: "LHeel", 22: "RBigToe", 23: "RSmallToe", 24: "RHeel"}
    POSE_PAIRS_BODY_25B = [[0, 1], [0, 2], [0, 17], [0, 18], [1, 3], [2, 4], [5, 7], [5, 17],
                               [6, 8], [6, 17], [7, 9], [8, 10], [11, 13], [11, 17], [12, 14], [12, 17],
                               [13, 15], [14, 16], [15, 21], [16, 24], [19, 20], [20, 21], [22, 23], [23, 24]]



    # 신경 네트워크의 구조를 지정하는 prototxt 파일 (다양한 계층이 배열되는 방법 등)
    protoFile_mpi = "mpi.prototxt"


    # 훈련된 모델의 weight 를 저장하는 caffemodel 파일
    weightsFile_mpi = "pose_iter_160000.caffemodel"

    # 비디오 경로
    man = f
    # 키포인트를 저장할 빈 리스트

    returnData = output_keypoints_with_lines_video(proto_file=protoFile_mpi, weights_file=weightsFile_mpi, video_path=man,
                                      threshold=0.05, BODY_PARTS=BODY_PARTS_BODY_25B, POSE_PAIRS=POSE_PAIRS_BODY_25B)




    print(returnData)
    return returnData

