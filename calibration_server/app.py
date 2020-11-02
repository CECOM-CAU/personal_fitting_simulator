from flask import Flask, render_template, request
from werkzeug.utils import secure_filename
import calibration_chess
import calibration_resize
import make_model
import cv2
import numpy as np
import socket
#import matplotlib.pyplot as plt
from keras.models import load_model
import test
app = Flask(__name__, static_url_path='/static')


@app.route('/')
def hello_world():
    return '<h1>File upload test</h1>'

@app.route('/test')
def testPage():
    return render_template("test.html")

@app.route('/fileUpload', methods=['POST'])
def fileUpload():
    if request.method == 'POST':
        f = request.files['file']
        f.save("testFile/" + secure_filename(f.filename))
        return '<h1>success upload</h1>'
    return '<h1>GET methods</h1>'


@app.route('/getCalibrationData', methods=['GET'])
def getCalibrationPage():
    return render_template("calibration.html")

@app.route('/getCalibrationData', methods=['POST'])
def getCalibrationData():
    if request.method == 'POST':
        f = request.files['file0']
        f.save("calibration_temp/" + secure_filename(f.filename))
        f = request.files['file1']
        f.save("calibration_temp/" + secure_filename(f.filename))
        f = request.files['file2']
        f.save("calibration_temp/" + secure_filename(f.filename))
        f = request.files['file3']
        f.save("calibration_temp/" + secure_filename(f.filename))
        f = request.files['file4']
        f.save("calibration_temp/" + secure_filename(f.filename))
        mtx, dist = calibration_chess.calibration_chess('calibration_temp/*.jpg')
        return '<h1>'+mtx+'</h1> <h1>'+dist+'</h1>'
    return '<h1>GET methods</h1>'

@app.route('/getModelData', methods=['GET'])
def getModelDataPage():
    return render_template("model_data.html")

@app.route('/getModelData', methods=['POST'])
def getModelData():
    if request.method == 'POST':
        f = request.files['file']
        f.save("makemodel_temp/" + secure_filename(f.filename))

        calibration_img = calibration_resize.image_resize('makemodel_temp/'+secure_filename(f.filename))
        make_model.make_model(calibration_img)

        IMG_WIDTH, IMG_HEIGHT = 256, 256
        IMG_PATH = 'calibration_test/testfile_resize.jpg'
        model = load_model('unet.h5')

        img = cv2.imread(IMG_PATH, cv2.IMREAD_COLOR)
        img_ori = cv2.cvtColor(img.copy(), cv2.COLOR_BGR2RGB)

        # plt.figure(figsize=(16, 16))
        # plt.imshow(img_ori)

        img = test.preprocess(img)
        input_img = img.reshape((1, IMG_WIDTH, IMG_HEIGHT, 3)).astype(np.float32) / 255
        pred = model.predict(input_img)

        mask = test.postprocess(img_ori, pred)

        # plt.figure(figsize=(16, 16))
        # plt.subplot(1, 2, 1)
        # plt.imshow(pred[0, :, :, 1])
        # plt.subplot(1, 2, 2)
        # plt.imshow(mask)

        converted_mask = cv2.cvtColor(mask, cv2.COLOR_GRAY2BGR)
        result_img = cv2.subtract(converted_mask, img_ori)
        result_img = cv2.subtract(converted_mask, result_img)
        # plt.figure(figsize=(16, 16))
        # plt.imshow(result_img)

        # plt.figure(figsize=(16, 16))
        return_img, arm_length, body_length, chest_length, chestToBody_length, chestToShoulder_length = test.calculatePose(
            result_img, peopleLength=1630)

        cv2.imwrite('imgs/test.jpg', return_img)
        print("arm_length=" + str(arm_length))
        print("body_length=" + str(body_length))

        # plt.imshow()
        # plt.show()
        return '<h1>arm:' + str(arm_length) + '</h1>' + '<h1>body:' + str(body_length) + '</h1>' + '<h1>chest:' + str(
            chest_length) + '</h1>' + '<h1>chestTobody:' + str(
            chestToBody_length) + '</h1>' + '<h1>chestToShoulder:' + str(chestToShoulder_length) + '</h1>'

    #return '<h1>modelData = temp</h1>'
    return '<h1>GET methods</h1>'

@app.route('/modelTest', methods=['GET'])
def getModelTestPage():
    return render_template("model_test.html")

@app.route('/modelTest', methods=['POST'])
def getModelTest():
    if request.method == 'POST':
        f = request.files['file']
        f.save("imgs/" + secure_filename("testData.jpg"))

        IMG_WIDTH, IMG_HEIGHT = 256, 256
        IMG_PATH = 'imgs/testData.jpg'
        model = load_model('unet.h5')

        img = cv2.imread(IMG_PATH, cv2.IMREAD_COLOR)
        img_ori = cv2.cvtColor(img.copy(), cv2.COLOR_BGR2RGB)

        #plt.figure(figsize=(16, 16))
        #plt.imshow(img_ori)

        img = test.preprocess(img)
        input_img = img.reshape((1, IMG_WIDTH, IMG_HEIGHT, 3)).astype(np.float32) / 255
        pred = model.predict(input_img)

        mask = test.postprocess(img_ori, pred)

        # plt.figure(figsize=(16, 16))
        # plt.subplot(1, 2, 1)
        # plt.imshow(pred[0, :, :, 1])
        # plt.subplot(1, 2, 2)
        # plt.imshow(mask)

        converted_mask = cv2.cvtColor(mask, cv2.COLOR_GRAY2BGR)
        result_img = cv2.subtract(converted_mask, img_ori)
        result_img = cv2.subtract(converted_mask, result_img)
        #plt.figure(figsize=(16, 16))
        #plt.imshow(result_img)

        #plt.figure(figsize=(16, 16))
        return_img, arm_length, body_length,chest_length, chestToBody_length, chestToShoulder_length = test.calculatePose(result_img, peopleLength=1630)

        cv2.imwrite('imgs/test.jpg', return_img)
        print("arm_length="+str(arm_length))
        print("body_length="+str(body_length))

        #plt.imshow()
        #plt.show()
        return '<h1>arm:' + str(arm_length) + '</h1>' + '<h1>body:' + str(body_length) + '</h1>'+ '<h1>chest:' + str(chest_length) + '</h1>' +'<h1>chestTobody:' + str(chestToBody_length) + '</h1>' +'<h1>chestToShoulder:' + str(chestToShoulder_length) + '</h1>'

    return '<h1>GET methods</h1>'

if __name__ == '__main__':
    IP = str(socket.gethostbyname(socket.gethostname()))
    app.run(host="165.194.44.20", port=5000, debug=False)
    app.run()
