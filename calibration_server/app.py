from flask import Flask, render_template, request
from werkzeug.utils import secure_filename
import calibration_chess
import calibration_resize
import make_model
app = Flask(__name__)


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
        f = request.files['file0']
        f.save("makemodel_temp/" + secure_filename(f.filename))

        calibration_img = calibration_resize.calibration_resize('makemodel_temp/'+secure_filename(f.filename))
        make_model.make_model(calibration_img)
        return '<h1>modelData = temp</h1>'
    return '<h1>GET methods</h1>'

if __name__ == '__main__':
    app.run()