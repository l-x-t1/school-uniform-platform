var certTestSuit = new TestSuit("证书接口");

certTestSuit.AddTest({
    name: "重新加载",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq();
        var oldCerts=null;
        seq.then(function (){
           new HebcaMiddleware.Middleware({
               success:function (middleware){
                   m=middleware
                   seq.resolve()
               },
               fail:function (e){
                   seq.reject(e)
               }
           })
        }).then(function () {
            m.getCerts({
                success:function (certs) {
                    oldCerts=certs;
                    seq.resolve();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).then(function () {
            m.reload({
                success:function () {
                    seq.resolve();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            });
        }).then(function (seq,d) {
            m.getCerts({
                success:function (certs) {
                    Assert.AssertEqualAsyn(testResult,oldCerts.length,certs.length,"重新加载前后证书个数不一致");
                    testResult.done();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            });
        }).onerror(function (e) {
            testResult.fail(e.message);
        }).run();
    }
});

certTestSuit.AddTest({
    name: "重置状态",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq();
        var oldCerts=null;
        seq.then(function () {
            m.getCerts({
                success:function (certs) {
                    oldCerts=certs;
                    if(oldCerts.length===0){
                        seq.reject(new Error("找不到证书"));
                    }else {
                        seq.resolve();
                    }

                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).then(function () {
            m.login({
                sn: oldCerts[0].sn,
                password: keyPassword,
                success:function () {
                    seq.resolve();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).then(function () {
            m.getCertInfo({
                sn: oldCerts[0].sn,
                success:function (cert) {
                    Assert.AssertEqualAsyn(testResult,true,cert.isAppLogined,"证书登录状态错误");
                    seq.resolve();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            });
        }).then(function () {
            m.reset({
                success:function () {
                    seq.resolve();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).then(function (seq,d) {
            m.getCertInfo({
                sn: oldCerts[0].sn,
                success:function (cert) {
                    Assert.AssertEqualAsyn(testResult,false,cert.isAppLogined,"证书登录状态错误");
                    testResult.done();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            });
        }).onerror(function (e) {
            testResult.fail(e.message);
        }).run();
    }
});

certTestSuit.AddTest({
    name: "获取证书列表",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq();
        var allCert=null;
        var signCert=null;
        var cryptCert=null;
        seq.then(function () {
            m.getCerts({
                certType:"all",
                success:function (certs) {
                    allCert=certs;
                    seq.resolve();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).then(function () {
            m.getCerts({
                certType:"sign",
                success:function (certs) {
                    signCert=certs;
                    seq.resolve();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            });
        }).then(function (seq,d) {
            m.getCerts({
                certType:"crypt",
                success:function (certs) {
                    cryptCert=certs;
                    Assert.AssertEqualAsyn(testResult,allCert.length,cryptCert.length+signCert.length,"证书个数错误");
                    Trace.TraceLn(JSON.stringify(allCert));
                    testResult.done();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            });
        }).onerror(function (e) {
            testResult.fail(e.message);
        }).run();
    }
});

certTestSuit.AddTest({
    name: "获取证书列表-获取主题特定OID",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq();
        seq.then(function () {
            m.getCerts({
                certType:"all",
                subjectOids:["2.5.4.42"],
                success:function (certs) {
                    for(var i=0;i<certs.length;i++){
                        if(certs[i].subject.g!=null){
                            Assert.AssertEqualAsyn(testResult,certs[i].subject.g,certs[i].subjectOids[0].values[0],"获取证书信息错误");
                        }

                    }
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

function forEachSignCert(testResult,callback){
    m.getCerts({
        success:function (certs) {
            var seq=new HebcaMiddleware.Seq();
            seq.thenEach(certs,function (seq,index) {
                callback(certs[index],function () {
                    seq.resolve(index+1);
                },function (e) {
                    seq.reject(e);
                });
            }).then(function () {
                    testResult.done();
                })
                .onerror(function (e) {
                    testResult.fail(e.message);
                })
                .run(0)
        },
        fail:function (e) {
            testResult.fail(e.message)
        }
    })
}

function forEachCryptCert(testResult,callback){
    m.getCerts({
        certType:"crypt",
        success:function (certs) {
            var seq=new HebcaMiddleware.Seq();
            seq.thenEach(certs,function (seq,index) {
                callback(certs[index],function () {
                    seq.resolve(index+1);
                },function (e) {
                    seq.reject(e);
                });
            }).then(function () {
                testResult.done();
            })
                .onerror(function (e) {
                    testResult.fail(e.message);
                })
                .run(0)
        },
        fail:function (e) {
            testResult.fail(e.message)
        }
    })
}

certTestSuit.AddTest({
    name: "获取证书信息",
    asyn: true,
    fun: function (testResult) {
       forEachSignCert(testResult,function (cert,resovle,reject) {
           m.getCertInfo({
               sn: cert.sn,
               subjectOids:["2.5.4.42"],
               success:function (certInfo) {
                   Assert.AssertEqualAsyn(testResult,cert.subject.cn,certInfo.subject.cn,"获取证书信息错误");
                   Assert.AssertEqualAsyn(testResult,cert.subject.g,certInfo.subject.g,"获取证书信息错误");
                   Assert.AssertEqualAsyn(testResult,cert.sn,certInfo.sn,"获取证书信息错误");
                   if(cert.subject.g!=null) {
                       Assert.AssertEqualAsyn(testResult, cert.subject.g, certInfo.subjectOids[0].values[0], "获取证书信息错误");
                   }
                   Trace.TraceLn(JSON.stringify(certInfo));
                   resovle()
               },
               fail: function (e) {
                   reject(e);
               }
           })
       });
    }
});

certTestSuit.AddTest({
    name: "选择证书-原生界面",
    asyn: true,
    fun: function (testResult) {
        HebcaMiddleware.setUi(null);
        m.selectCert({
            success:function (cert) {
                Trace.TraceLn("选中证书："+cert.subject.cn);
                testResult.done();
            },
            fail:function (e) {
                testResult.fail(e.message);
            }
        })
    }
});

certTestSuit.AddTest({
    name: "选择证书-JS库",
    asyn: true,
    fun: function (testResult) {
        HebcaMiddleware.setUi(HebcaMiddleware.uiBootstrap);
        m.selectCert({
            success:function (cert) {
                Trace.TraceLn("选中证书："+cert.subject.cn);
                testResult.done();
            },
            fail:function (e) {
                testResult.fail(e.message);
            }
        })
    }
});

certTestSuit.AddTest({
    name: "签名验签",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq();
        seq.then(function () {
            m.sign({
                data:"12345678",
                password: keyPassword,
                success:function (r) {
                    seq.resolve(r);
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).then(function (seq,r) {
            m.verifySign({
                cert: r.cert,
                signature: r.signature,
                data: "12345678",
                success:function () {
                    testResult.done();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).onerror(function (e) {
            testResult.fail(e);
        }).run();
    }
});

certTestSuit.AddTest({
    name: "签名验签-兼容性",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq();
        seq.then(function () {
            m.verifySign({
                cert: "MIIDPjCCAuOgAwIBAgIIdB8A5gAUnggwDAYIKoEcz1UBg3UFADBmMQswCQYDVQQGEwJDTjEOMAwGA1UECAwFaGViZWkxFTATBgNVBAcMDHNoaWppYXpodWFuZzEOMAwGA1UECgwFaGViY2ExDjAMBgNVBAsMBWhlYmNhMRAwDgYDVQQDDAdIQlNNMkNBMB4XDTE2MDcxMzE2MDAwMFoXDTE4MDcxNDE1NTk1OVowgcIxCzAJBgNVBAYTAkNOMRIwEAYDVQQIDAnmsrPljJfnnIExDjAMBgNVBAoMBWhlYmNhMQ4wDAYDVQQLDAVoZWJjYTEpMCcGA1UECwwgNDAyODkwYTA0YmU3N2JmMTAxNGJlNzk3ODVkNTAwMDIxIjAgBgNVBCoMGea1i+ivleWPkeivgeWuieijhTYxMTI3ODcxEjAQBgNVBAEMCTA5NjgyNTkzODEcMBoGA1UEAwwT5rWL6K+V5Y+R6K+B5a6J6KOFNjBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABCERSqzSENocEzCvE4lCT3OzCsJHJJShdsKW37UzXahI5LwKMG9a7eqfbX7vHma7AGlUkYdUVDQavjDPxz9u4dCjggEaMIIBFjAMBgNVHRMEBTADAQEAMB0GA1UdJQQWMBQGCCsGAQUFBwMCBggrBgEFBQcDBDALBgNVHQ8EBAMCAMAwEQYJYIZIAYb4QgEBBAQDAgCAMB8GA1UdIwQYMBaAFHowvaXh9jXCrV0PZtV1Wbo+hZUdMD0GA1UdHwQ2MDQwMqAwoC6GLGh0dHA6Ly9jcmwuaGViY2EuY29tL2NybGRvd25sb2FkL0hCU00yQ0EuY3JsMEgGCCsGAQUFBwEBBDwwOjA4BggrBgEFBQcwAoYsaHR0cDovL2NybC5oZWJjYS5jb20vY3JsZG93bmxvYWQvSEJTTTJDQS5jZXIwHQYDVR0OBBYEFCRgIEirZFf7UgLItCheCRnT5lwXMAwGCCqBHM9VAYN1BQADRwAwRAIgJOrWhNjEuDS9ZYEDyzxQYY8uqwtywzjZbOIG2HfNW14CIF9ItMmLETD+74OC5dJQGb+R8KC2hO9BY9HJGWZJ9Pnd",
                signature: "MEQCIBG5pupWw9M/gaSJ12sKQJXLpYo/HeaQ7F4ldZcRHf+dAiB8iRXCgBpQs4xHtVdtX5ZqbdEhmtU6+1UZj0sfU/5Sxg==",
                data: "123456",
                success:function () {
                    testResult.done();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).then(function () {
            m.sign({
                data:"12345678",
                password: keyPassword,
                success:function (r) {
                    seq.resolve(r);
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).then(function (seq,r) {
            m.verifySign({
                cert: r.cert,
                signature: r.signature,
                data: "12345678",
                success:function () {
                    testResult.done();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).onerror(function (e) {
            testResult.fail(e);
        }).run();
    }
});

certTestSuit.AddTest({
    name: "签名验签-遍历证书",
    asyn: true,
    fun: function (testResult) {
        forEachSignCert(testResult,function (cert,resovle,reject) {
            var seq=new HebcaMiddleware.Seq();
            seq.then(function () {
                m.sign({
                    sn: cert.sn,
                    data:"12345678",
                    password: keyPassword,
                    success:function (r) {
                        seq.resolve(r);
                    },
                    fail:function (e) {
                        seq.reject(e);
                    }
                })
            }).then(function (seq,r) {
                m.verifySign({
                    cert: r.cert,
                    signature: r.signature,
                    data: "12345678",
                    success:function () {
                        resovle();
                    },
                    fail:function (e) {
                        seq.reject(e);
                    }
                })
            }).onerror(function (e) {
                reject(e);
            }).run();
        });

    }
});

certTestSuit.AddTest({
    name: "登录登出",
    asyn: true,
    fun: function (testResult) {
        forEachSignCert(testResult,function (cert,resovle,reject) {
            var seq=new HebcaMiddleware.Seq();
            seq.then(function () {
                m.login({
                    sn:cert.sn,
                    password:keyPassword,
                    success:function () {
                        seq.resolve()
                    },
                    fail:function (e) {
                        seq.reject(e);
                    }
                })
            }).then(function (seq,r) {
                m.getCertInfo({
                    sn:cert.sn,
                    success:function (certInfo) {
                        Assert.AssertEqualAsyn(testResult,true,certInfo.isAppLogined,"登录状态错误");
                        seq.resolve()
                    },
                    fail:function (e) {
                        seq.reject(e);
                    }
                })
            }).then(function () {
                m.logout({
                    sn:cert.sn,
                    success:function () {
                        seq.resolve()
                    },
                    fail:function (e) {
                        seq.reject(e);
                    }
                })
            }).then(function (seq,r) {
                m.getCertInfo({
                    sn:cert.sn,
                    success:function (certInfo) {
                        Assert.AssertEqualAsyn(testResult,false,certInfo.isAppLogined,"登录状态错误");
                        resovle();
                    },
                    fail:function (e) {
                        seq.reject(e);
                    }
                })
            }).onerror(function (e) {
                reject(e);
            }).run();
        });
    }
});

certTestSuit.AddTest({
    name: "证书加密和解密",
    asyn: true,
    fun: function (testResult) {
        forEachCryptCert(testResult,function (cert,resovle,reject) {
            var seq=new HebcaMiddleware.Seq();
            seq.then(function () {
                m.certEncrypt({
                    cert: cert.cert,
                    data: "12345678",
                    success: function (encryptData) {
                        seq.resolve(encryptData);
                    },
                    fail:function (e) {
                        seq.reject(e);
                    }
                })
            }).then(function (seq,encryptData) {
                m.certDecrypt({
                    sn: cert.sn,
                    encryptData: encryptData,
                    password:keyPassword,
                    success:function (data) {
                        Assert.AssertEqualAsyn(testResult,"12345678",data,"证书解密错误");
                        resovle();
                    },
                    fail:function (e) {
                        seq.reject(e);
                    }
                })
            }).onerror(function (e) {
                reject(e);
            }).run();
        });
    }
});

function genAndWrapperAndImprotAndCrypt(testResult,cert,alg,iv,padding,resovle,reject){
    var keysGen=null;
    var data_encrypt=null;
    var seq=new HebcaMiddleware.Seq();
    seq.then(function () {
        m.genAndWrapperKey({
            certs: [cert.cert],
            alg: alg,
            success: function (keys) {
                keysGen=keys;
                seq.resolve(keys);
            },
            fail:function (e) {
                seq.reject(e);
            }
    })
    }).then(function (seq,keys) {
        m.encrypt({
            keyId: keys.keyId,
            data: "1234567812345678",
            iv:iv===null?null:HebcaMiddleware.base64.encode(iv),
            padding:padding,
            success:function (encryptData) {
                data_encrypt=encryptData;
                seq.resolve();
            },
            fail:function (e) {
                seq.reject(e);
            }
        })
    }).then(function () {
        m.closeKey({
            keyId: keysGen.keyId,
            success:function () {
                seq.resolve();
            },
            fail:function (e) {
                seq.reject(e);
            }
        })
    }).then(function () {
        m.importWrapperKey({
            alg:alg,
            wrapperKey: keysGen.wrapperKeys[0],
            success:function (keyId) {
                seq.resolve(keyId);
            },
            fail:function (e) {
                seq.reject(e);
            }
        })
    }).then(function (seq,keyId) {
        m.decrypt({
            keyId:keyId,
            encryptData: data_encrypt,
            iv:iv===null?null:HebcaMiddleware.base64.encode(iv),
            padding:padding,
            success:function (data) {
                Assert.AssertEqualAsyn(testResult,"1234567812345678",data,"解密错误");
                seq.resolve(keyId);
            },
            fail:function (e) {
                seq.reject(e);
            }
        })
    }).then(function (seq,keyId) {
        m.closeKey({
            keyId: keyId,
            success:function () {
                resovle();
            },
            fail:function (e) {
                seq.reject(e);
            }
        })
    }).onerror(function (e) {
        reject(e);
    }).run();
}

certTestSuit.AddTest({
    name: "生成密钥并导出导入以及加密解密-SM4_ECB",
    asyn: true,
    fun: function (testResult) {
        forEachCryptCert(testResult,function (cert,resovle,reject) {
            genAndWrapperAndImprotAndCrypt(testResult,cert,"SM4_ECB", null, "no",resovle,reject);
        });
    }
});

certTestSuit.AddTest({
    name: "生成密钥并导出导入以及加密解密-SM4_CBC",
    asyn: true,
    fun: function (testResult) {
        forEachCryptCert(testResult,function (cert,resovle,reject) {
            genAndWrapperAndImprotAndCrypt(testResult,cert,"SM4_CBC","1234567812345678","pkcs5",resovle,reject);
        });
    }
});

certTestSuit.AddTest({
    name: "生成密钥并导出导入以及加密解密-SM1_ECB",
    asyn: true,
    fun: function (testResult) {
        forEachCryptCert(testResult,function (cert,resovle,reject) {
            genAndWrapperAndImprotAndCrypt(testResult,cert,"SM1_ECB",null,"no",resovle,reject);
        });
    }
});

certTestSuit.AddTest({
    name: "生成密钥并导出导入以及加密解密-SM1_CBC",
    asyn: true,
    fun: function (testResult) {
        forEachCryptCert(testResult,function (cert,resovle,reject) {
            genAndWrapperAndImprotAndCrypt(testResult,cert,"SM1_CBC","1234567812345678","pkcs5",resovle,reject);
        });
    }
});

function genAndWrapperAndImprotAndCryptFile(testResult,cert,alg,iv,padding,resovle,reject){
    var keysGen=null;
    var data_encrypt=null;
    var seq=new HebcaMiddleware.Seq();
    seq.then(function () {
        m.genAndWrapperKey({
            certs: [cert.cert],
            alg: alg,
            success: function (keys) {
                keysGen=keys;
                seq.resolve(keys);
            },
            fail:function (e) {
                seq.reject(e);
            }
        })
    }).then(function (seq,keys) {
        m.encryptFile({
            keyId: keys.keyId,
            file: fileRoot+"files/1.txt",
            encryptFile: fileRoot+"files/1.txt.encrypt",
            iv:HebcaMiddleware.base64.encode(iv),
            padding:padding,
            success:function () {
                seq.resolve();
            },
            fail:function (e) {
                seq.reject(e);
            }
        })
    }).then(function () {
        m.closeKey({
            keyId: keysGen.keyId,
            success:function () {
                seq.resolve();
            },
            fail:function (e) {
                seq.reject(e);
            }
        })
    }).then(function () {
        m.importWrapperKey({
            alg:alg,
            wrapperKey: keysGen.wrapperKeys[0],
            success:function (keyId) {
                seq.resolve(keyId);
            },
            fail:function (e) {
                seq.reject(e);
            }
        })
    }).then(function (seq,keyId) {
        m.decryptFile({
            keyId:keyId,
            file: fileRoot+"files/1.txt.decrypt",
            encryptFile: fileRoot+"files/1.txt.encrypt",
            iv:HebcaMiddleware.base64.encode(iv),
            padding:padding,
            success:function (data) {
                seq.resolve(keyId);
            },
            fail:function (e) {
                seq.reject(e);
            }
        })
    }).then(function (seq,keyId) {
        m.closeKey({
            keyId: keyId,
            success:function () {
                seq.resolve();
            },
            fail:function (e) {
                seq.reject(e);
            }
        })
    }).then(function () {
        m.digestFile({
            alg: "sm3",
            file: fileRoot+"files/1.txt",
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
            file: fileRoot+"files/1.txt.decrypt",
            success: function (digest2) {
                Assert.AssertEqualAsyn(testResult,digest1,digest2,"加解密结果不一致");
                seq.resolve();
            },
            fail:function (e) {
                seq.reject(e);
            }
        });
    }).then(function () {
        resovle();
    }).onerror(function (e) {
        reject(e);
    }).run();
}


certTestSuit.AddTest({
    name: "生成密钥并导出导入以及加密解密-文件-SM4_CBC",
    asyn: true,
    fun: function (testResult) {
        forEachCryptCert(testResult,function (cert,resovle,reject) {
            genAndWrapperAndImprotAndCryptFile(testResult,cert,"SM4_CBC","1234567812345678","pkcs5",resovle,reject);
        });
    }
});

certTestSuit.AddTest({
    name: "生成密钥并导出导入以及加密解密-文件-SM1_CBC",
    asyn: true,
    fun: function (testResult) {
        forEachCryptCert(testResult,function (cert,resovle,reject) {
            genAndWrapperAndImprotAndCryptFile(testResult,cert,"SM1_CBC","1234567812345678","pkcs5",resovle,reject);
        });
    }
});



allTestes.AddTestSuit(certTestSuit);
