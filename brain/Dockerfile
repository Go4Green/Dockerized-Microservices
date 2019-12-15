FROM ubuntu:16.04

RUN apt-get update && apt-get install -y wget ca-certificates \
    git curl vim python3-dev python3-pip \
    libfreetype6-dev libpng12-dev libhdf5-dev
RUN pip3 install --upgrade pip
RUN pip3 install tensorflow
RUN pip3 install numpy pandas sklearn matplotlib h5py pyyaml scipy
RUN pip3 install git+https://github.com/fchollet/keras.git
RUN pip3 install scikit-fuzzy
RUN apt-get update
RUN apt-get install -y python3-tk
RUN apt-get update
RUN apt-get install libevent-dev -y
RUN pip install greenlet gevent connexion
RUN echo "/usr/local/lib" >> /etc/ld.so.conf
RUN /sbin/ldconfig
RUN mkdir -p /app

WORKDIR app
