from keras.models import load_model
import cv2
import numpy as np

import os

def getSegImage(img):
    import tensorflow as tf
    from tf_bodypix.api import download_model, load_model, BodyPixModelPaths
    import cv2
    import test
    cv2.imwrite("temp.jpg",img)
    bodypix_model = load_model(download_model(
        # BodyPixModelPaths.MOBILENET_FLOAT_50_STRIDE_16
        BodyPixModelPaths.MOBILENET_RESNET50_FLOAT_STRIDE_32

    ))

    image = tf.keras.preprocessing.image.load_img(
        'temp.jpg'
    )

    image_array = tf.keras.preprocessing.image.img_to_array(image)
    result = bodypix_model.predict_single(image_array)

    mask = result.get_mask(threshold=0.6)
    tf.keras.preprocessing.image.save_img(
        'test_image/output-mask.jpg',
        mask
    )

    """
    IMG_WIDTH, IMG_HEIGHT = 256, 256
    #IMG_PATH = 'imgs/testData.jpg'
    model = load_model('unet.h5')

    #img = cv2.imread(img_path, cv2.IMREAD_COLOR)
    img_ori = cv2.cvtColor(img.copy(), cv2.COLOR_BGR2RGB)

    img = preprocess(img)
    input_img = img.reshape((1, IMG_WIDTH, IMG_HEIGHT, 3)).astype(np.float32) / 255
    pred = model.predict(input_img)

    mask = postprocess(img_ori, pred)

    converted_mask = cv2.cvtColor(mask, cv2.COLOR_GRAY2BGR)
    result_img = cv2.subtract(converted_mask, img_ori)
    result_img = cv2.subtract(converted_mask, result_img)
    """
    return cv2.imread("test_image/output-mask.jpg")


def preprocess(img):
    IMG_WIDTH, IMG_HEIGHT = 256, 256
    im = np.zeros((IMG_WIDTH, IMG_HEIGHT, 3), dtype=np.uint8)
    if img.shape[0] >= img.shape[1]:
        scale = img.shape[0] / IMG_HEIGHT
        new_width = int(img.shape[1] / scale)
        diff = (IMG_WIDTH - new_width) // 2
        img = cv2.resize(img, (new_width, IMG_HEIGHT))
        im[:, diff:diff + new_width, :] = img
    else:
        scale = img.shape[1] / IMG_WIDTH
        new_height = int(img.shape[0] / scale)
        diff = (IMG_HEIGHT - new_height) // 2
        img = cv2.resize(img, (IMG_WIDTH, new_height))
        im[diff:diff + new_height, :, :] = img
    return im

def postprocess(img_ori, pred):
    THRESHOLD = 0.9
    EROSION = 1
    h, w = img_ori.shape[:2]
    mask_ori = (pred.squeeze()[:, :, 1] > THRESHOLD).astype(np.uint8)
    max_size = max(h, w)
    result_mask = cv2.resize(mask_ori, dsize=(max_size, max_size))
    if h >= w:
        diff = (max_size - w) // 2
        if diff > 0:
            result_mask = result_mask[:, diff:-diff]
    else:
        diff = (max_size - h) // 2
        if diff > 0:
            result_mask = result_mask[diff:-diff, :]

    result_mask = cv2.resize(result_mask, dsize=(w, h))
    # fill holes
    #     cv2.floodFill(result_mask, mask=np.zeros((h+2, w+2), np.uint8), seedPoint=(0, 0), newVal=255)
    #     result_mask = cv2.bitwise_not(result_mask)
    result_mask *= 255
    #     # erode image
    #     element = cv2.getStructuringElement(cv2.MORPH_RECT, (2*EROSION + 1, 2*EROSION+1), (EROSION, EROSION))
    #     result_mask = cv2.erode(result_mask, element)
    # smoothen edges
    result_mask = cv2.GaussianBlur(result_mask, ksize=(9, 9), sigmaX=5, sigmaY=5)

    return result_mask
def output_keypoints(source_img,frame_drawn, proto_file, weights_file, threshold, BODY_PARTS, points):
    #global points


    # 네트워크 불러오기
    net = cv2.dnn.readNetFromCaffe(proto_file, weights_file)

    # GPU 사용
    net.setPreferableBackend(cv2.dnn.DNN_BACKEND_CUDA)
    net.setPreferableTarget(cv2.dnn.DNN_TARGET_CUDA)

    # 입력 이미지의 사이즈 정의
    image_height = 368
    image_width = 368

    # 네트워크에 넣기 위한 전처리
    input_blob = cv2.dnn.blobFromImage(source_img, 1.0 / 255, (image_width, image_height), (0, 0, 0),
                                       swapRB=False, crop=False)

    # 전처리된 blob 네트워크에 입력
    net.setInput(input_blob)

    # 결과 받아오기
    out = net.forward()
    # The output is a 4D matrix :
    # The first dimension being the image ID ( in case you pass more than one image to the network ).
    # The second dimension indicates the index of a keypoint.
    # The model produces Confidence Maps and Part Affinity maps which are all concatenated.
    # For COCO model it consists of 57 parts – 18 keypoint confidence Maps + 1 background + 19*2 Part Affinity Maps. Similarly, for MPI, it produces 44 points.
    # We will be using only the first few points which correspond to Keypoints.
    # The third dimension is the height of the output map.
    out_height = out.shape[2]
    # The fourth dimension is the width of the output map.
    out_width = out.shape[3]

    # 원본 이미지의 높이, 너비를 받아오기
    frame_height, frame_width = source_img.shape[:2]

    # 포인트 리스트 초기화
    points.clear()

    #print('│' + " POINT ".center(90, '─') + '│')
    for i in range(len(BODY_PARTS)):

        # 신체 부위의 confidence map
        prob_map = out[0, i, :, :]

        # 최소값, 최대값, 최소값 위치, 최대값 위치
        min_val, prob, min_loc, point = cv2.minMaxLoc(prob_map)

        # 원본 이미지에 맞게 포인트 위치 조정
        x = int((frame_width * point[0]) / out_width)
        y = int((frame_height * point[1]) / out_height)

        if prob > threshold:  # [pointed]
            cv2.circle(frame_drawn, (x, y), 5, (0, 255, 255), thickness=-1, lineType=cv2.FILLED)
            cv2.putText(frame_drawn, str(i), (x, y), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 0, 255), 1, lineType=cv2.LINE_AA)

            points.append((x, y))
            #print('{0}│'.format(
                #f"│ [pointed] {BODY_PARTS[i]} ({i}) => prob: {prob:.5f} / x: {x} / y: {y}".ljust(91, ' ')))

        else:  # [not pointed]
            cv2.circle(frame_drawn, (x, y), 5, (0, 255, 255), thickness=-1, lineType=cv2.FILLED)
            cv2.putText(frame_drawn, str(i), (x, y), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 0, 0), 1,
                        lineType=cv2.LINE_AA)

            points.append(None)
            #print('{0}│'.format(
                #f"│ [not pointed] {BODY_PARTS[i]} ({i}) => prob: {prob:.5f} / x: {x} / y: {y}".ljust(91, ' ')))

def output_keypoints_with_lines(frame_drawn, POSE_PAIRS, delta, points):
    #print('│' + " LINK ".center(90, '─') + '│')
    for pair in POSE_PAIRS:
        part_a = pair[0]  # 0 (Head)
        part_b = pair[1]  # 1 (Neck)
        if points[part_a] and points[part_b]:
            delta[(part_a, part_b)] =  -1*(points[part_a][0] - points[part_b][0])/(points[part_a][1] - points[part_b][1]+0.00001)
            #print('{0}│'.format(f"│ [linked] {part_a} {points[part_a]} <=> {part_b} {points[part_b]}".ljust(91, ' ')))
            cv2.line(frame_drawn, points[part_a], points[part_b], (0, 255, 0), 2)
        #else:
            #print('{0}│'.format(f"│ [not linked] {part_a} {points[part_a]} <=> {part_b} {points[part_b]}".ljust(91, ' ')))

def extractPose(img,frame_drawn):
    BODY_PARTS_BODY_25B = {0: "Nose", 1: "LEye", 2: "REye", 3: "LEar", 4: "REar", 5: "LShoulder", 6: "RSoulder",
                           7: "LElbow", 8: "RElbow", 9: "LWrist", 10: "RWrist", 11: "LHip", 12: "RHip", 13: "LKnee",
                           14: "RKnee", 15: "LAnkle", 16: "RAnkle", 17: "Neck", 18: "Head", 19: "LBigToe",
                           20: "LSmallToe", 21: "LHeel", 22: "RBigToe", 23: "RSmallToe", 24: "RHeel"}
    POSE_PAIRS_BODY_25B = [[0, 1], [0, 2], [0, 17], [0, 18], [1, 3], [2, 4], [5, 7], [5, 17],
                           [6, 8], [6, 17], [7, 9], [8, 10], [11, 13], [11, 17], [12, 14], [12, 17],
                           [13, 15], [14, 16], [15, 21], [16, 24], [19, 20], [20, 21], [22, 23], [23, 24]]

    points = []
    delta = {}

    output_keypoints(source_img=img, frame_drawn=frame_drawn, proto_file='mpi.prototxt',
                     weights_file='pose_iter_160000.caffemodel',
                     threshold=0.02, BODY_PARTS=BODY_PARTS_BODY_25B, points=points)
    output_keypoints_with_lines(frame_drawn=frame_drawn, POSE_PAIRS=POSE_PAIRS_BODY_25B, points=points, delta=delta)
    length = ((points[24][1] - points[18][1]) + (points[21][1] - points[18][1])) / 2
    return frame_drawn

def calculatePose(img, frame_drawn = None, peopleLength = 1630):

    BODY_PARTS_BODY_25B = {0: "Nose", 1: "LEye", 2: "REye", 3: "LEar", 4: "REar", 5: "LShoulder", 6: "RSoulder",
                           7: "LElbow", 8: "RElbow", 9: "LWrist", 10: "RWrist", 11: "LHip", 12: "RHip", 13: "LKnee",
                           14: "RKnee", 15: "LAnkle", 16: "RAnkle", 17: "Neck", 18: "Head", 19: "LBigToe",
                           20: "LSmallToe", 21: "LHeel", 22: "RBigToe", 23: "RSmallToe", 24: "RHeel"}
    POSE_PAIRS_BODY_25B = [[0, 1], [0, 2], [0, 17], [0, 18], [1, 3], [2, 4], [5, 7], [5, 17],
                           [6, 8], [6, 17], [7, 9], [8, 10], [11, 13], [11, 17], [12, 14], [12, 17],
                           [13, 15], [14, 16], [15, 21], [16, 24], [19, 20], [20, 21], [22, 23], [23, 24]]

    points = []
    delta = {}

    if frame_drawn.all() == None :
        frame_drawn = img.copy()  # 텍스트 및 테두리가 그려질 프레임
    else :
        temp_height, temp_width, temp_channel = img.shape
        print(img.shape)
        img = cv2.resize(img, dsize=(temp_width,temp_height), interpolation=cv2.INTER_AREA)

    output_keypoints(source_img=img,frame_drawn=frame_drawn,proto_file='mpi.prototxt', weights_file='pose_iter_160000.caffemodel',
                     threshold=0.1, BODY_PARTS=BODY_PARTS_BODY_25B,points=points)
    output_keypoints_with_lines(frame_drawn=frame_drawn,POSE_PAIRS=POSE_PAIRS_BODY_25B,points=points,delta=delta)
    length = ((points[24][1] - points[18][1]) + (points[21][1] - points[18][1]))/2
    pixelPerMM = peopleLength/length

    point1 = []
    point2 = []
    delta_10 = abs((points[12][1]+points[11][1])/2 - points[17][1]) / 100

    #body_delta = getDelta(points[12],points[11]) +0.00001
    bodyLength = 0
    chestLength = 0
    prev_bodyLength = 0
    chestToBodyLength = 0
    chestToShoulderLength = 0
    y_count = 0
    for y in range(1,100):
        x_body = (points[12][0] + points[11][0]) // 2
        prev_y = points[12][1] - int(delta_10 * (y-1))
        prev_x = x_body
        delta_x = 10
        y_body = points[12][1] - int(delta_10 * (y))
        while True:
            cv2.line(frame_drawn, (prev_x,prev_y),(x_body,y_body), (0, 255, 0), 2)
            prev_y = y_body
            prev_x = x_body

            x_body -= delta_x
            color = frame_drawn[y_body,x_body]

            if np.array_equal(frame_drawn[y_body,x_body-10], np.array([0,0,0])):
                point1 = [x_body,y_body]
                break
        if(x_body > 3000):
            break
        x_body = (points[12][0] + points[11][0]) // 2
        prev_y = points[12][1] - int(delta_10 * (y - 1))
        prev_x = (points[12][0]+points[11][0])//2

        delta_x = -10
        while True:
            cv2.line(frame_drawn, (prev_x,prev_y), (x_body,y_body), (0, 255, 0), 2)
            prev_y = y_body
            prev_x = x_body
            x_body -= delta_x
            color = frame_drawn[y_body,x_body]
            #print("color("+str(x_body) + "),("+str(y_body)+")=" + str(color) + str(type(color)))
            if np.array_equal(frame_drawn[y_body,x_body+10], np.array([0,0,0])):
                point2 = [x_body, y_body]
                break
        tempLength = np.sqrt((point1[0]-point2[0])**2 + (point1[1]-point2[1])**2)
        print("length(" + str(point1[0]) + "),(" + str(point2[0]) + ")=" + str(tempLength))
        if prev_bodyLength == 0:
            prev_bodyLength = tempLength
        if y == 2:
            bodyLength = tempLength
        if(tempLength - prev_bodyLength > 85):
            chestLength = prev_bodyLength
            chestToBodyLength = abs(points[11][1] - y_body)
            chestToShoulderLength = abs(points[5][1] - y_body)
            y_count = y
            break
        else:
            #bodyLength += tempLength
            prev_bodyLength = tempLength

    print("body length:" + str(bodyLength*pixelPerMM))
    print("chest length:" + str(chestLength * pixelPerMM))
    print("chestToBody length:" + str(chestToBodyLength * pixelPerMM))
    print("chestToShoulder length:" +str(chestToShoulderLength * pixelPerMM))
    start_point = 7
    end_point = 9
    delta_10 = (points[end_point][0] - points[start_point][0]) / 10
    armLength = 0
    for x in range(1,10):
        prev_x = points[start_point][0] + int(delta_10*(x-1))
        prev_y = points[start_point][1] + int(delta_10*(x-1)*-1/delta[(start_point, end_point)])
        delta_x = 15
        x_7 = points[start_point][0] + int(delta_10*(x))
        y_7 = points[start_point][1] + int(delta_10*(x)*-1/delta[(start_point, end_point)])
        while True:
            cv2.line(frame_drawn, (prev_x,prev_y), (x_7,y_7), (0, 255, 0), 2)
            prev_x = x_7
            prev_y = y_7
            x_7 += delta_x
            y_7 += int(delta[(start_point,end_point)]*delta_x)
            color = frame_drawn[y_7,x_7]
            print("color("+str(x_7) + "),("+str(y_7)+")=" + str(color) + str(type(color)))
            if np.array_equal(frame_drawn[y_7,x_7], np.array([0,0,0])):
                point1 = [x_7,y_7]
                break
        delta_x = -20
        while True:
            cv2.line(frame_drawn, (prev_x,prev_y), (x_7,y_7), (0, 255, 0), 2)
            prev_x = x_7
            prev_y = y_7
            x_7 += delta_x
            y_7 += int(delta[(start_point,end_point)]*delta_x)
            color = frame_drawn[y_7,x_7]
            print("color("+str(x_7) + "),("+str(y_7)+")=" + str(color) + str(type(color)))
            if np.array_equal(frame_drawn[y_7,x_7], np.array([0,0,0])):
                point2 = [x_7, y_7]
                break
        armLength += np.sqrt((point1[0]-point2[0])**2 + (point1[1]-point2[1])**2)

    print("arm length:" + str(armLength/9*pixelPerMM))
    return frame_drawn, armLength/9*pixelPerMM, bodyLength*pixelPerMM, chestLength*pixelPerMM,chestToBodyLength*pixelPerMM, chestToShoulderLength*pixelPerMM

def getDelta(point1, point2):
    return (point1[1] - point2[1]) / (point1[0] - point2[0] + 0.00001)

if cv2.waitKey(0):  # 아무 키나 입력 시 종료
    cv2.destroyAllWindows()

