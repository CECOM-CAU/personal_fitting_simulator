
import numpy as np

import cv2

import glob
import sys


def calibration_chess(imgs_path):
    criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 30, 0.001)
    objp = np.zeros((6 * 7, 3), np.float32)
    objp[:, :2] = np.mgrid[0:7, 0:6].T.reshape(-1, 2)
    # Arrays to store object points and image points from all the images.
    objpoints = []  # 3d point in real world space
    imgpoints = []  # 2d points in image plane.
    images = glob.glob(imgs_path)  # 파일에 있는 jpg 모두 불러오기
    for fname in images:
        img = cv2.imread(fname)
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        # Find the chess board corners
        ret, corners = cv2.findChessboardCorners(gray, (7, 6), None)
        # If found, add object points, image points (after refining them)
        if ret == True:
            objpoints.append(objp)
            corners2 = cv2.cornerSubPix(gray, corners, (11, 11), (-1, -1), criteria)
            imgpoints.append(corners2)
            # Draw and display the corners
            img = cv2.drawChessboardCorners(img, (7, 6), corners2, ret)
            # cv2.imshow('img',img)
            cv2.waitKey(500)

    # stdoutOrigin=sys.stdout
    # sys.stdout=open("result.txt","w")#왜곡계수들 txt파일에 저장
    # 카메라 매크릭스와 왜곡계수
    ret, mtx, dist, rvecs, tvecs = cv2.calibrateCamera(objpoints, imgpoints, gray.shape[::-1], None, None)
    return mtx, dist
    #np.save("./calibration_chess/result/savemtx.npy", mtx)  # mtx 저장
    #np.save("./calibration_chess/result/savedist.npy", dist)  # dist 저장

    cv2.destroyAllWindows()
