import os
import json
from flask import Flask, flash, request, redirect, url_for, jsonify, session
from werkzeug.utils import secure_filename

rootDir = os.path.abspath(os.path.dirname(__file__))
UPLOAD_FOLDER = rootDir + '/upload'
ALLOWED_EXTENSIONS = set(['txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'])

app = Flask(__name__)
app.secret_key = "super_secret_key"
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/', methods=['GET', 'POST'])
def upload_file():
    print('Root : ' + rootDir)
    if request.method == 'POST':
        # check if the post request has the file part
        images = request.files.getlist("image[]")
        for image in images:
            print('info [file ' + image.filename + ', '+ image.name +']')
            if image and allowed_file(image.filename):
                filename = secure_filename(image.filename)
                image.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
    return  '''
    <!doctype html>
    <title>Upload new File</title>
    <h1>Upload new File</h1>
    <form method=post enctype=multipart/form-data>
      <input type=file name='image[]'> <br/>
      <input type=file name='image[]'> <br/>
      <input type=file name='image[]'> <br/>
      <input type=file name='image[]'> <br/> <br/>
      <input type=submit value=Upload>
    </form>
    '''

 # return jsonify(
 #        username="",
 #        email="",
 #        id=""
 #    )

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5001, debug=True)