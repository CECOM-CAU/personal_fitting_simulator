import numpy as np
import cv2
import glob
from PIL import Image


def image_resize(img_path):
    img=Image.open(img_path)
    (img_h,img_w)=img.size
    unit=img_w/3
    grid=(0,0,img_h,unit)
    cropped_img1=img.crop(grid)
    cropped_img1.save('calibration_test/testfile_crop1.jpg')
    grid=(0,unit,img_h,unit*2)
    cropped_img2=img.crop(grid)
    cropped_img2.save('calibration_test/testfile_crop2.jpg')
    grid=(0,unit*2,img_h,unit*3)
    cropped_img3=img.crop(grid)
    cropped_img3.save('calibration_test/testfile_crop3.jpg')
    img=cv2.imread("calibration_test/testfile_crop1.jpg")
    img1=cv2.resize(img, None,None,1,0.8,interpolation=cv2.INTER_AREA)
    cv2.imwrite('calibration_test/testfile_crop1.jpg', img1)
    img=cv2.imread("calibration_test/testfile_crop2.jpg")
    img2=cv2.resize(img, None,None,1,1,interpolation=cv2.INTER_AREA)
    cv2.imwrite('calibration_test/testfile_crop2.jpg', img2)
    img=cv2.imread("calibration_test/testfile_crop3.jpg")
    img3=cv2.resize(img, None,None,1,0.8,interpolation=cv2.INTER_AREA)
    cv2.imwrite('calibration_test/testfile_crop3.jpg', img3)
    addv=np.vstack((img1,img2))
    addv=np.vstack((addv,img3))
    cv2.imwrite('calibration_test/testfile_resize.jpg', addv)
    #K = np.load("calibration_chess/result/savemtx.npy")  # 저장되어 있는 mtx 파일 불러오기
    #dist = np.load("calibration_chess/result/savedist.npy")  # 저장되어 있는 dist 파일 불러오기
    """
    image = cv2.imread('calibration_test/testfile_resize.jpg')  # 보정할 사진파일
    images = glob.glob('calibration_test/testfile_resize.jpg')  # 보정할 사진파일

    for fname in images:
        img = cv2.imread(fname)
        h, w = img.shape[:2]

        # undistort
        #newcamera, roi = cv2.getOptimalNewCameraMatrix(K, dist, (w, h), 0)
        #newimg = cv2.undistort(img, K, dist, None, newcamera)

        # save image
        newfname = fname + '_undis.png'
        # cv2.imwrite('./calibration_test/CalibResult/undis_result.jpg', newimg)
        #cv2.imwrite(newfname, newimg)
        cv2.imwrite(newfname, img)
        """