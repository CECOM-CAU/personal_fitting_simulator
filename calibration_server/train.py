

from keras.callbacks import ModelCheckpoint, ReduceLROnPlateau
from keras.models import Model
from keras.layers import Input, concatenate, Dropout, Reshape, Permute, Activation, ZeroPadding2D, Cropping2D, Add
from keras.layers.convolutional import Conv2D, MaxPooling2D, UpSampling2D, Conv2DTranspose, DepthwiseConv2D
from keras.regularizers import l2
from keras.preprocessing.image import ImageDataGenerator
import numpy as np

#%%

IMG_HEIGHT = 256
IMG_WIDTH = 256
BATCH_SIZE = 16

x_train = np.load('data/x_train.npz')['data'].astype(np.float32)
y_train = np.load('data/y_train.npz')['data'].astype(np.float32)
x_val = np.load('data/x_val.npz')['data'].astype(np.float32)
y_val = np.load('data/y_val.npz')['data'].astype(np.float32)

print(x_train.shape, y_train.shape)
print(x_val.shape, y_val.shape)

#%%

train_datagen = ImageDataGenerator(
    rescale=1./255,
    brightness_range=[0.7, 1.3]
)

val_datagen = ImageDataGenerator(
    rescale=1./255
)

train_gen = train_datagen.flow(
    x_train,
    y_train,
    batch_size=BATCH_SIZE,
    shuffle=True
)

val_gen = val_datagen.flow(
    x_val,
    y_val,
    batch_size=BATCH_SIZE,
    shuffle=False
)

#%%

inputs = Input(shape=(IMG_HEIGHT, IMG_WIDTH, 3))

# encode
conv1 = Conv2D(32, 3, activation='relu', padding='same', kernel_initializer='orthogonal')(inputs)
# conv1 = Dropout(0.2)(conv1)
conv1 = Conv2D(64, 3, activation='relu', padding='same', kernel_initializer='orthogonal')(conv1)
pool1 = MaxPooling2D(pool_size=2)(conv1)
# (128, 128, 64)

shortcut_1 = pool1

conv2 = Conv2D(64, 3, activation='relu', padding='same', kernel_initializer='orthogonal')(pool1)
# conv2 = Dropout(0.2)(conv2)
conv2 = Conv2D(128, 3, activation='relu', padding='same', kernel_initializer='orthogonal')(conv2)
pool2 = MaxPooling2D(pool_size=2)(conv2)
# (64, 64, 128)

shortcut_2 = pool2

conv3 = Conv2D(128, 3, activation='relu', padding='same', kernel_initializer='orthogonal')(pool2)
# conv3 = Dropout(0.2)(conv3)
conv3 = Conv2D(256, 3, activation='relu', padding='same', kernel_initializer='orthogonal')(conv3)
pool3 = MaxPooling2D(pool_size=2)(conv3)
# (32, 32, 256)

# middle depthwiseconv nodes
shortcut_3 = pool3

mid = DepthwiseConv2D(3, activation='relu', padding='same', kernel_initializer='orthogonal')(pool3)
mid = Conv2D(256, 1, activation='relu', padding='same', kernel_initializer='orthogonal')(mid)

mid = DepthwiseConv2D(3, activation='relu', padding='same', kernel_initializer='orthogonal')(mid)
mid = Conv2D(256, 1, activation='relu', padding='same', kernel_initializer='orthogonal')(mid)

mid = Add()([shortcut_3, mid])

# decode
up8 = UpSampling2D(size=2)(mid)
up8 = concatenate([up8, conv3], axis=-1)
conv8 = Conv2D(128, 3, activation='relu', padding='same', kernel_initializer='orthogonal')(up8)
# conv8 = Dropout(0.2)(conv8)
conv8 = Conv2D(128, 3, activation='relu', padding='same', kernel_initializer='orthogonal')(conv8)
# (64, 64, 128)

conv8 = Add()([shortcut_2, conv8])

up9 = UpSampling2D(size=2)(conv8)
up9 = concatenate([up9, conv2], axis=-1)
conv9 = Conv2D(64, 3, activation='relu', padding='same', kernel_initializer='orthogonal')(up9)
# conv9 = Dropout(0.2)(conv9)
conv9 = Conv2D(64, 3, activation='relu', padding='same', kernel_initializer='orthogonal')(conv9)
# (128, 128, 64)

conv9 = Add()([shortcut_1, conv9])

up10 = UpSampling2D(size=2)(conv9)
up10 = concatenate([up10, conv1], axis=-1)
conv10 = Conv2D(32, 3, activation='relu', padding='same', kernel_initializer='orthogonal')(up10)
# conv10 = Dropout(0.2)(conv10)
conv10 = Conv2D(32, 3, activation='relu', padding='same', kernel_initializer='orthogonal')(conv10)
# (256, 256, 32)

conv11 = Conv2D(2, 1, padding='same', activation='relu',
                kernel_initializer='he_normal', kernel_regularizer=l2(0.005))(conv10)
conv11 = Reshape((IMG_HEIGHT * IMG_WIDTH, 2))(conv11)
# (256, 256, 2)

conv11 = Activation('softmax')(conv11)

outputs = Reshape((IMG_HEIGHT, IMG_WIDTH, 2))(conv11)

model = Model(inputs=inputs, outputs=outputs)

model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])
model.summary()

#%%

history = model.fit_generator(train_gen,
    validation_data=val_gen,
    epochs=100,
    callbacks=[
        ModelCheckpoint('static/models/unet_no_drop.h5', monitor='val_acc', save_best_only=True, verbose=1),
        ReduceLROnPlateau(monitor='val_acc', factor=0.2, patience=10, verbose=1, min_lr=1e-05)

    ]
)

#%%
