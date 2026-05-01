var baseTestSuit = new TestSuit("基础接口");

baseTestSuit.AddTest({
    name: "Base64编码解码",
    fun: function(testResult){
        var b64=HebcaMiddleware.base64.encode("12345678");
        var src=HebcaMiddleware.base64.decode(b64);
        Assert.Assert(src==="12345678", "Base64编码解码");
    }
});

baseTestSuit.AddTest({
    name: "数据摘要算法",
    asyn: true,
    fun: function(testResult){
        var seq=new HebcaMiddleware.Seq();
        seq.then(function () {
            m.digest({
                data: "12345678",
                success: function (digest) {
                    Assert.AssertEqualAsyn(testResult,"D///gelx+j8JEHq/d5MUY/wHEL+4li7+rj1WVLBzuww=",digest,"SM3摘要");
                    seq.resolve();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            });
        }).then(function () {
            m.digest({
                alg: "sm3",
                data: "12345678",
                success: function (digest) {
                    Assert.AssertEqualAsyn(testResult,"D///gelx+j8JEHq/d5MUY/wHEL+4li7+rj1WVLBzuww=",digest,"SM3摘要");
                    seq.resolve();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            });
        }).then(function () {
            m.digest({
                alg:"sha1",
                data: "12345678",
                success: function (digest) {
                    Assert.AssertEqualAsyn(testResult,"fCIvspJ9goryL1khNOiTJIBjfA0=",digest,"SHA1摘要");
                    seq.resolve();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            });
        }).then(function () {
            m.digest({
                alg:"md5",
                data: "12345678",
                success: function (digest) {
                    Assert.AssertEqualAsyn(testResult,"JdVa0oOqQAr0ZMdtcTwHrQ==",digest,"MD5摘要");
                    seq.resolve();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            });
        }).then(function () {
            m.digest({
                alg:"sha256",
                data: "12345678",
                success: function (digest) {
                    Assert.AssertEqualAsyn(testResult,"73l8gRjwLftklgfdXT+MdiMEjJwGPVMsyVxe16iYpk8=",digest,"sha256摘要");
                    seq.resolve();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            });
        }).then(function () {
            m.digest({
                alg: "sha256",
                data: HebcaMiddleware.base64.encode("12345678"),
                dataFormat:"base64",
                success: function (digest) {
                    Assert.AssertEqualAsyn(testResult, "73l8gRjwLftklgfdXT+MdiMEjJwGPVMsyVxe16iYpk8=", digest, "sha256摘要-B64");
                    seq.resolve();
                },
                fail: function (e) {
                    seq.reject(e);
                }
            });
        })
            .then(function () {
            testResult.done();
        }).onerror(function (e) {
            testResult.fail(e.message);
        })
            .run();
    }
});

baseTestSuit.AddTest({
    name: "文件摘要算法",
    asyn: true,
    fun: function(testResult){
        var seq=new HebcaMiddleware.Seq();
        var algs=["sm3","sha1","md5","sha256"];
        var digestExp=[
            "sOOsS2Qqw2NWV0FF7bOUTyS7FpXx/zWMVQIVaNvoG6M=",
            "89VkfQR8aIflt6oR9JSpfd0f974=",
            "mw6xS/R+rZAHpAOyksVhwA==",
            "IRkd6aR7y+ZJ3gRgd80iSaawuHqb0KHk3HVj6wA2vbI="
        ];
        seq.thenEach(algs,function (seq,i) {
            m.digestFile({
                alg: algs[i],
                file: fileRoot+"files/digest.pdf",
                success: function (digest) {
                    Assert.AssertEqualAsyn(testResult,digestExp[i],digest,algs[i]+"摘要");
                    seq.resolve(i+1);
                },
                fail:function (e) {
                    seq.reject(e);
                }
            });
        }).then(function () {
            testResult.done();
        }).onerror(function (e) {
            testResult.fail(e.message);
        }).run(0);
    }
});

baseTestSuit.AddTest({
    name: "文件摘要算法-大文件",
    asyn: true,
    fun: function(testResult){
        var seq=new HebcaMiddleware.Seq();
        var algs=["sm3"];
        var digestExp=[
            "/GhWTBuiw9Dh0A4XUefRW2KVtRftgvk0BgFYhIns1rw=",
        ];
        seq.thenEach(algs,function (seq,i) {
            m.digestFile({
                alg: algs[i],
                file: fileRoot+"files/output.rar",
                progress:digestProgress,
                success: function (digest) {
                    Assert.AssertEqualAsyn(testResult,digestExp[i],digest,algs[i]+"摘要");
                    seq.resolve(i+1);
                },
                fail:function (e) {
                    seq.reject(e);
                }
            });
        }).then(function () {
            testResult.done();
        }).onerror(function (e) {
            testResult.fail(e.message);
        }).run(0);
    }
});

function encryptAndDecyptSoft_CBC(testResult,key,alg,iv){
    var seq=new HebcaMiddleware.Seq();
    seq.then(function () {
        m.encryptSoft({
            key: key,
            alg: alg,
            data: "12345678",
            iv: iv,
            padding: "pkcs5",
            success:function (encryptData) {
                seq.resolve(encryptData);
            },
            fail: function (e) {
                seq.reject(e);
            }
        })
    }).then(function (seq,encryptData) {
        m.decryptSoft({
            key: key,
            alg: alg,
            encryptData:encryptData,
            iv: iv,
            padding: "pkcs5",
            success:function (data) {
                seq.resolve(data);
            },
            fail: function (e) {
                seq.reject(e);
            }
        })
    }).then(function (seq,data) {
        Assert.AssertEqualAsyn(testResult,"12345678",data,"加解密-"+alg);
        seq.resolve();

    }).then(function () {
        testResult.done();
    }).onerror(function (e) {
        testResult.fail(e.message);
    }).run();
}

function encryptAndDecyptSoft_ECB(testResult,key,alg){
    var seq=new HebcaMiddleware.Seq();
    seq.then(function () {
        m.encryptSoft({
            key: key,
            alg: alg,
            data: "12345678123456781234567812345678",
            success:function (encryptData) {
                seq.resolve(encryptData);
            },
            fail: function (e) {
                seq.reject(e);
            }
        })
    }).then(function (seq,encryptData) {
        m.decryptSoft({
            key: key,
            alg: alg,
            encryptData:encryptData,
            success:function (data) {
                seq.resolve(data);
            },
            fail: function (e) {
                seq.reject(e);
            }
        })
    }).then(function (seq,data) {
        Assert.AssertEqualAsyn(testResult,"12345678123456781234567812345678",data,"加解密-"+alg);
        seq.resolve();

    }).then(function () {
        testResult.done();
    }).onerror(function (e) {
        testResult.fail(e.message);
    }).run();
}

baseTestSuit.AddTest({
    name: "对称加解密-软算法-SM4_CBC",
    asyn: true,
    fun: function (testResult) {
        encryptAndDecyptSoft_CBC(testResult,
            HebcaMiddleware.base64.encode("1234567812345678"),
            "SM4_CBC",
            HebcaMiddleware.base64.encode("1234567812345678"));
    }
});

baseTestSuit.AddTest({
    name: "对称加解密-软算法-DES_CBC",
    asyn: true,
    fun: function (testResult) {
        encryptAndDecyptSoft_CBC(testResult,
            HebcaMiddleware.base64.encode("12345678"),
            "DES_CBC",
            HebcaMiddleware.base64.encode("12345678"));
    }
});

baseTestSuit.AddTest({
    name: "对称加解密-软算法-DES3_CBC",
    asyn: true,
    fun: function (testResult) {
        encryptAndDecyptSoft_CBC(testResult,
            HebcaMiddleware.base64.encode("123456781234567812345678"),
            "DES3_CBC",
            HebcaMiddleware.base64.encode("12345678"));
    }
});

baseTestSuit.AddTest({
    name: "对称加解密-软算法-AES_CBC",
    asyn: true,
    fun: function (testResult) {
        encryptAndDecyptSoft_CBC(testResult,
            HebcaMiddleware.base64.encode("1234567812345678"),
            "AES_CBC",
            HebcaMiddleware.base64.encode("1234567812345678"));
    }
});

baseTestSuit.AddTest({
    name: "对称加解密-软算法-SM4_ECB",
    asyn: true,
    fun: function (testResult) {
        encryptAndDecyptSoft_ECB(testResult,
            HebcaMiddleware.base64.encode("1234567812345678"),
            "SM4_ECB");
    }
});

baseTestSuit.AddTest({
    name: "对称加解密-软算法-DES_ECB",
    asyn: true,
    fun: function (testResult) {
        encryptAndDecyptSoft_ECB(testResult,
            HebcaMiddleware.base64.encode("12345678"),
            "DES_ECB");
    }
});

baseTestSuit.AddTest({
    name: "对称加解密-软算法-DES3_ECB",
    asyn: true,
    fun: function (testResult) {
        encryptAndDecyptSoft_ECB(testResult,
            HebcaMiddleware.base64.encode("123456781234567812345678"),
            "DES3_ECB");
    }
});

baseTestSuit.AddTest({
    name: "对称加解密-软算法-AES_ECB",
    asyn: true,
    fun: function (testResult) {
        encryptAndDecyptSoft_ECB(testResult,
            HebcaMiddleware.base64.encode("1234567812345678"),
            "AES_ECB");
    }
});

function encryptAndDecyptSoftFile_CBC(testResult,key,alg,iv){
    var seq=new HebcaMiddleware.Seq();
    seq.then(function () {
        m.encryptSoftFile({
            key: key,
            alg: alg,
            file: fileRoot+"files/2.pdf",
            encryptFile:fileRoot+"files/2.encrypt",
            iv: iv,
            padding: "pkcs5",
            success:function (encryptData) {
                seq.resolve(encryptData);
            },
            fail: function (e) {
                seq.reject(e);
            }
        })
    }).then(function (seq,encryptData) {
        m.decryptSoftFile({
            key: key,
            alg: alg,
            encryptFile: fileRoot+"files/2.encrypt",
            file:fileRoot+"files/2.decrypt.pdf",
            iv: iv,
            padding: "pkcs5",
            success:function (data) {
                seq.resolve(data);
            },
            fail: function (e) {
                seq.reject(e);
            }
        })
    }).then(function () {
        m.digestFile({
            alg: "sm3",
            file: fileRoot+"files/2.pdf",
            success: function (digest) {
                seq.resolve(digest);
            },
            fail:function (e) {
                seq.reject(e);
            }
        });
    }).then(function (seq,digest1) {
        m.digestFile({
            alg: "sm3",
            file: fileRoot+"files/2.decrypt.pdf",
            success: function (digest2) {
                Assert.AssertEqualAsyn(testResult,digest1,digest2,"加解密结果不一致");
                seq.resolve();
            },
            fail:function (e) {
                seq.reject(e);
            }
        });
    }).then(function () {
        testResult.done();
    }).onerror(function (e) {
        testResult.fail(e.message);
    }).run();
}

baseTestSuit.AddTest({
    name: "对称加解密文件-软算法-SM4_CBC",
    asyn: true,
    fun: function (testResult) {
        encryptAndDecyptSoftFile_CBC(testResult,
            HebcaMiddleware.base64.encode("1234567812345678"),
            "SM4_CBC",
            HebcaMiddleware.base64.encode("1234567812345678"));
    }
});

baseTestSuit.AddTest({
    name: "对称加解密文件-软算法-DES_CBC",
    asyn: true,
    fun: function (testResult) {
        encryptAndDecyptSoftFile_CBC(testResult,
            HebcaMiddleware.base64.encode("12345678"),
            "DES_CBC",
            HebcaMiddleware.base64.encode("12345678"));
    }
});

baseTestSuit.AddTest({
    name: "对称加解密文件-软算法-DES3_CBC",
    asyn: true,
    fun: function (testResult) {
        encryptAndDecyptSoftFile_CBC(testResult,
            HebcaMiddleware.base64.encode("123456781234567812345678"),
            "DES3_CBC",
            HebcaMiddleware.base64.encode("12345678"));
    }
});

baseTestSuit.AddTest({
    name: "对称加解密文件-软算法-AES_CBC",
    asyn: true,
    fun: function (testResult) {
        encryptAndDecyptSoftFile_CBC(testResult,
            HebcaMiddleware.base64.encode("1234567812345678"),
            "AES_CBC",
            HebcaMiddleware.base64.encode("1234567812345678"));
    }
});

baseTestSuit.AddTest({
    name: "对称加解密-软算法-兼容性",
    asyn: true,
    fun: function (testResult) {
        var datas=[
            {
                alg:"DES_ECB",
                key: "KUXz70j8nmw=",
                encryptData: "+XO1iRfjDV75c7WJF+MNXvl/Cg+aFJHe",
                iv: null,
                decryptData: "1234567812345678",
                padding: "pkcs5",
            },
            {
                alg:"DES_CBC",
                key: "RX5CbuchWdc=",
                encryptData: "yVjjr6zPNh0aD/oRqCmIBqwItELtSaK2",
                iv: HebcaMiddleware.base64.encode("12345678"),
                decryptData: "1234567812345678",
                padding: "pkcs5"
            },
            {
                alg:"DES3_ECB",
                key: "eV+RmH8kVe+0o2s0L+zbgIEqESfzubeT",
                encryptData: "58JhPjfQ2CrnwmE+N9DYKjmc2YJm5d5m",
                iv: null,
                decryptData: "1234567812345678",
                padding: "pkcs5"
            },
            {
                alg:"DES3_CBC",
                key: "183QC9ZI9s5PfCpJd20lTUvAX4/19Yxj",
                encryptData: "ojFmWCLOectahx0KuyOCPn1iWyLfEnlE",
                iv: HebcaMiddleware.base64.encode("12345678"),
                decryptData: "1234567812345678",
                padding: "pkcs5"
            },
            {
                alg:"AES_ECB",
                key: "oZHI8nPoY2yT/3EN5hFPLA==",
                encryptData: "DQaRcebiiq/9dZvzLr3IRxto+EPv4YxuSkYTSZsCc7E=",
                iv: null,
                decryptData: "1234567812345678",
                padding: "pkcs5"
            },
            {
                alg:"AES_CBC",
                key: "VD/QyO2OqMtbOqk0s3IKEQ==",
                encryptData: "Gx84b9LERfFfZ/OnQvaLUyOjnzlJoBeMai0HjkcJKzE=",
                iv: HebcaMiddleware.base64.encode("1234567812345678"),
                decryptData: "1234567812345678",
                padding: "pkcs5"
            },
            {
                alg:"SM4_ECB",
                key: "ZcmYkij0taEegmpCBnLFrQ==",
                encryptData: "lrJzUPL3lJ9++RyAHZaQaLVH+e52UmPDtjUjchc5DEs=",
                iv: null,
                decryptData: "1234567812345678",
                padding: "pkcs5"
            },
            {
                alg:"SM4_CBC",
                key: "cfaeel9SC1tWzcECWM6W3w==",
                encryptData: "uxho6ZEv+6Ry7pDWau6bCy8lSFM5KeWlhRhCi+zplIQ=",
                iv: HebcaMiddleware.base64.encode("1234567812345678"),
                decryptData: "1234567812345678",
                padding: "pkcs5"
            },
        ];

        var seq=new HebcaMiddleware.Seq();
        seq.thenEach(datas,function (seq,index) {
            m.decryptSoft({
                key: datas[index].key,
                alg: datas[index].alg,
                encryptData:datas[index].encryptData,
                iv: datas[index].iv,
                padding: datas[index].padding,
                success:function (data) {
                    Assert.AssertEqualAsyn(testResult,datas[index].decryptData,data,"解密结果不正确");
                    seq.resolve(index+1);
                },
                fail: function (e) {
                    seq.reject(e);
                }
            })
        }).then(function () {
            testResult.done();
        }).onerror(function (e) {
            testResult.fail(e.message)
        })
            .run(0);
    }
});

function encryptProgress(currentLength,allLength,next,cancel) {
    HebcaMiddleware.uiBootstrap.progress({
        title: "正在加密文件...",
        currentLength:currentLength,
        allLength:allLength,
        success:function () {
            next();
        },
        fail:function (e) {
            cancel();
        }
    });
}

function decryptProgress(currentLength,allLength,next,cancel) {
    HebcaMiddleware.uiBootstrap.progress({
        title: "正在解密文件...",
        currentLength:currentLength,
        allLength:allLength,
        success:function () {
            next();
        },
        fail:function (e) {
            cancel();
        }
    });
}

function digestProgress(currentLength,allLength,next,cancel) {
    HebcaMiddleware.uiBootstrap.progress({
        title: "正在摘要文件...",
        currentLength:currentLength,
        allLength:allLength,
        success:function () {
            next();
        },
        fail:function (e) {
            cancel();
        }
    });
}

function digestCompareProgress(currentLength,allLength,next,cancel) {
    HebcaMiddleware.uiBootstrap.progress({
        title: "正在校验文件摘要...",
        currentLength:currentLength,
        allLength:allLength,
        success:function () {
            next();
        },
        fail:function (e) {
            cancel();
        }
    });
}

function encryptAndDecyptSoftBigFile_CBC(testResult,key,alg,iv){
    var seq=new HebcaMiddleware.Seq();
    seq.then(function () {
        m.encryptSoftFile({
            key: key,
            alg: alg,
            file: fileRoot+"files/output.rar",
            encryptFile:fileRoot+"files/output.encrypt",
            iv: iv,
            padding: "pkcs5",
            progress: encryptProgress,
            success:function (encryptData) {
                seq.resolve(encryptData);
            },
            fail: function (e) {
                seq.reject(e);
            }
        })
    }).then(function (seq,encryptData) {
        setTimeout(function () {
            m.decryptSoftFile({
                key: key,
                alg: alg,
                encryptFile: fileRoot+"files/output.encrypt",
                file:fileRoot+"files/output.decrypt.rar",
                iv: iv,
                padding: "pkcs5",
                progress: decryptProgress,
                success:function (data) {
                    seq.resolve(data);
                },
                fail: function (e) {
                    seq.reject(e);
                }
            })
        },100);
    }).then(function () {
        m.digestFile({
            alg: "sm3",
            file: fileRoot+"files/output.rar",
            progress:digestCompareProgress,
            success: function (digest) {
                seq.resolve(digest);
            },
            fail:function (e) {
                seq.reject(e);
            }
        });
    }).then(function (seq,digest1) {
        m.digestFile({
            alg: "sm3",
            file: fileRoot+"files/output.decrypt.rar",
            progress:digestCompareProgress,
            success: function (digest2) {
                Assert.AssertEqualAsyn(testResult,digest1,digest2,"加解密结果不一致");
                seq.resolve();
            },
            fail:function (e) {
                seq.reject(e);
            }
        });
    }).then(function () {
        testResult.done();
    }).onerror(function (e) {
        testResult.fail(e.message);
    }).run();
}

baseTestSuit.AddTest({
    name: "对称加解密大文件-软算法-SM4_CBC",
    asyn: true,
    fun: function (testResult) {
        encryptAndDecyptSoftBigFile_CBC(
            testResult,
            HebcaMiddleware.base64.encode("1234567812345678"),
            "SM4_CBC",
            HebcaMiddleware.base64.encode("1234567812345678")
        );
    }
});

baseTestSuit.AddTest({
    name: "文件读写",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq();
        seq.then(function () {
            m.writeFile({
                file: fileRoot+"files/2.txt",
                data: "123456",
                success:function (f) {
                    seq.resolve();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            });
        }).then(function () {
            m.readFile({
                file: fileRoot+"files/2.txt",
                success:function (data) {
                    seq.resolve(data);
                },
                fail:function (e) {
                    seq.reject(e);
                }
            });
        }).then(function (seq,d) {
            Assert.AssertEqualAsyn(testResult,"123456",d,"文件读写结果不一致");
            testResult.done();
        }).onerror(function (e) {
            testResult.fail(e.message);
        }).run();
    }

});

baseTestSuit.AddTest({
    name: "上传下载文件",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq();
        seq.then(function () {
            m.uploadFile({
                file: fileRoot+"files/2.txt",
                url: demoServer+"/demo/upload",
                success:function () {
                    seq.resolve();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            });
        }).then(function () {
            m.downloadFile({
                file: fileRoot+"files/3.txt",
                url: demoServer+"/demo/download/2.txt",
                success:function () {
                    seq.resolve();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            });
        }).then(function (seq,d) {
            m.readFile({
                file: fileRoot+"files/2.txt",
                success:function (data) {
                    seq.resolve(data);
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).then(function (seq,d) {
            m.readFile({
                file: fileRoot+"files/3.txt",
                success:function (data) {
                    Assert.AssertEqualAsyn(testResult,d,data,"文件读写结果不一致");
                    testResult.done();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).onerror(function (e) {
            testResult.fail(e.message);
        }).run();
    }
});


baseTestSuit.AddTest({
    name: "上传下载大文件",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq();
        seq.then(function () {
            m.uploadFile({
                file: fileRoot+"files/1.zip",
                url: demoServer+"/demo/upload",
                progress:function(currentLength,allLength,next,cancel){
                    HebcaMiddleware.uiBootstrap.progress({
                        title: "正在上传文件...",
                        currentLength:currentLength,
                        allLength:allLength,
                        success:function () {
                            next();
                        },
                        fail:function (e) {
                            cancel();
                        }
                    });
                },
                success:function () {
                    seq.resolve();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            });
        }).then(function (){
            setTimeout(function (){
                seq.resolve()
            },1000)
        }).then(function () {
            m.downloadFile({
                file: fileRoot+"files/2.zip",
                url: demoServer+"/demo/download/1.zip",
                progress:function(currentLength,allLength,next,cancel){
                    HebcaMiddleware.uiBootstrap.progress({
                        title: "正在下载文件...",
                        currentLength:currentLength,
                        allLength:allLength,
                        success:function () {
                            next();
                        },
                        fail:function (e) {
                            cancel();
                        }
                    });
                },
                success:function () {
                    seq.resolve();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            });
        }).then(function (seq,d) {
            m.digestFile({
                file: fileRoot+"files/1.zip",
                success:function (data) {
                    seq.resolve(data);
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).then(function (seq,d) {
            m.digestFile({
                file: fileRoot+"files/2.zip",
                success:function (data) {
                    Assert.AssertEqualAsyn(testResult,d,data,"上传下载文件的数据摘要结果不一致");
                    testResult.done();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).onerror(function (e) {
            testResult.fail(e.message);
        }).run();
    }
});

baseTestSuit.AddTest({
    name: "时间戳签署和验证",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq();
        seq.then(function () {
            m.signTimeStamp({
                data: "12345678",
                success:function (t){
                    seq.resolve(t)
                },
                fail: function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,t){
            m.verifyTimeStamp({
                data: "12345678",
                timeStamp: t,
                success:function (){
                    seq.resolve(t)
                },
                fail: function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,t){
            m.getTimeStampInfo({
                timeStamp: t,
                success:function (info){
                    Assert.AssertAsyn(testResult,info.time.length!==0,"时间戳时间为空","TimeStamp")
                    Assert.AssertAsyn(testResult,info.cert.length!==0,"时间戳证书为空","TimeStamp")
                    testResult.done()
                },
                fail: function (e){
                    seq.reject(e)
                }
            })
        }).onerror(function (e){
            testResult.fail(e)
        }).run()
    }
});

allTestes.AddTestSuit(baseTestSuit);
