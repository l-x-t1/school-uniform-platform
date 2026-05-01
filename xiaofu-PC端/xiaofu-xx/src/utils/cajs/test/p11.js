var p11TestSuit = new TestSuit("与P11中间件互操作");

p11TestSuit.AddTest({
    name: "p11签名，go验证",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq();
        seq.then(function () {
            p11.signContent({
                content: "12345678",
                pin: "123456",
                success:function (ret) {
                    Trace.TraceLn(JSON.stringify(ret))
                    seq.resolve(ret);
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).then(function (seq,ret) {
            m.verifySign({
                data:"12345678",
                cert:ret.cert,
                signature:ret.signature,
                success:function () {
                    testResult.done()
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

p11TestSuit.AddTest({
    name: "p11非对称加密，go非对称解密",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq();
        seq.then(function () {
            p11.selectCryptCert({
                success:function () {
                    seq.resolve();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).then(function () {
            p11.encrypt({
                content: HebcaMiddleware.base64.encode("12345678"),
                success:function (ret) {
                    Trace.TraceLn(JSON.stringify(ret))
                    seq.resolve(ret);
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).then(function (seq,ret) {
            m.certDecrypt({
                encryptData:ret,
                password:"123456",
                success:function (data) {
                    if(data==="12345678"){
                        testResult.done()
                    }
                    else{
                        seq.reject(new Error("解密后数据错误"))
                    }
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


p11TestSuit.AddTest({
    name: "go非对称加密，p11非对称解密",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq();
        seq.then(function () {
            m.getCerts({
                certType: "crypt",
                success:function (certs){
                    if(certs.length===0){
                        seq.reject(new Error("找不到证书"))
                    }
                    else{
                        seq.resolve(certs[0].cert)
                    }
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,c) {
            m.certEncrypt({
                cert:c,
                data:"12345678",
                success:function (en) {
                    seq.resolve(en);
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).then(function (seq,en) {
            p11.decrypt({
                content: en,
                success:function (data) {
                    if(HebcaMiddleware.base64.decode(data)==="12345678"){
                        testResult.done()
                    }
                    else{
                        seq.reject(new Error("解密后数据错误"))
                    }
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

p11TestSuit.AddTest({
    name: "p11-p7签名，go-p7验证",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq();
        seq.then(function () {
            p11.p7Sign({
                content: HebcaMiddleware.base64.encode("12345678"),
                success:function (p7) {
                    seq.resolve(p7);
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).then(function (seq,p7) {
            m.verifyMessage({
                message:p7,
                success:function () {
                    testResult.done()
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

p11TestSuit.AddTest({
    name: "go-p7签名(兼容格式)，p11-p7验证",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq();
        seq.then(function () {
            m.signMessage({
                data: HebcaMiddleware.base64.encode("12345678"),
                dataFormat: "base64",
                password:"123456",
                compatP11: true,
                success:function (p7) {
                    seq.resolve(p7);
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).then(function (seq,p7) {
            p11.p7VerifySign({
                pkcs7:p7,
                success:function (result) {
                    if(result){
                        testResult.done()
                    }
                    else {
                        seq.reject(new Error("验证失败"))
                    }
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

p11TestSuit.AddTest({
    name: "p11-p7加密，go-p7解密",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq();
        seq.then(function (){
            p11.selectCryptCert({
                success:function () {
                    seq.resolve();
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).then(function (){
            p11.getCertB64({
                success:function (cert) {
                    seq.resolve(cert);
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).then(function (seq,cert) {
            p11.p7Envelop({
                content: HebcaMiddleware.base64.encode("12345678"),
                certs: [cert],
                success:function (p7) {
                    seq.resolve(p7);
                },
                fail:function (e) {
                    seq.reject(e);
                }
            })
        }).then(function (seq,p7) {
            m.unenvelopeMessage({
                message:p7,
                success:function (data) {
                    Trace.TraceLn(data)
                    if(data==="12345678"){
                        testResult.done()
                    }
                    else {
                        seq.reject(new Error("解密后数据不正确"))
                    }
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


function goP7EncryptP11Decrypt(testResult,alg,encryptUseDevice){
    var seq=new HebcaMiddleware.Seq();
    seq.then(function (){
        m.getCerts({
            certType:"crypt",
            success:function (certs){
                if(certs.length===0){
                    seq.reject(new Error("没有找到证书"))
                }
                seq.resolve(certs[0].cert)
            },
            fail:function (e){
                seq.reject(e)
            }
        })
    }).then(function (seq,cert){
        m.envelopeMessage({
            data: "12345678",
            password: "123456",
            certs: [cert],
            alg: alg,
            compatP11: true,
            encryptUseDevice: encryptUseDevice,
            success: function(p7) {
                seq.resolve(p7);
            },
            fail:function (e) {
                seq.reject(e);
            }
        })
    }).then(function (seq,p7){
        p11.p7UnEnvelop({
            pkcs7:p7,
            success:function (data) {
                Trace.TraceLn(data)
                if(data===HebcaMiddleware.base64.encode("12345678")){
                    testResult.done()
                }else{
                    seq.reject(new Error("解密后数据不正确"));
                }
            },
            fail:function (e) {
                seq.reject(e);
            }
        })
    }).onerror(function (e) {
        testResult.fail(e.message);
    }).run();
}

p11TestSuit.AddTest({
    name: "go-p7加密(DES3_CBC,兼容格式)，p11-p7解密",
    asyn: true,
    fun: function (testResult) {
        goP7EncryptP11Decrypt(testResult,"DES3_CBC", false)
    }
});

p11TestSuit.AddTest({
    name: "go-p7加密(SM4_CBC,兼容格式)，p11-p7解密",
    asyn: true,
    fun: function (testResult) {
        goP7EncryptP11Decrypt(testResult,"SM4_CBC", false)
    }
});

p11TestSuit.AddTest({
    name: "go-p7加密(SM4_CBC硬算法,兼容格式)，p11-p7解密",
    asyn: true,
    fun: function (testResult) {
        goP7EncryptP11Decrypt(testResult,"SM4_CBC", true)
    }
});


allTestes.AddTestSuit(p11TestSuit);