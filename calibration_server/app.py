from flask import Flask, render_template, request
from werkzeug.utils import secure_filename

app = Flask(__name__)


@app.route('/')
def hello_world():
    return 'File upload test'

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
if __name__ == '__main__':
    app.run()