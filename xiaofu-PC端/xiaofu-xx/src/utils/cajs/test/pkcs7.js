var pkcs7TestSuit = new TestSuit("PKCS7接口");

pkcs7TestSuit.AddTest({
    name: "p7签名",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq()
        seq.then(function (){
            m.signMessage({
                data:"12345678",
                success:function (message){
                    seq.resolve(message)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,message){
            m.getMessageInfo({
                message: message,
                success:function (info){
                    Assert.AssertEqualAsyn(testResult,"12345678", info.content,"p7content")
                    Assert.AssertAsyn(testResult, info.signCert.length>0,"p7signCert")
                    Assert.AssertAsyn(testResult, info.timeStamp.length===0,"p7timeStamp")
                    seq.resolve(message)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,message){
            m.verifyMessage({
                message: message,
                success:function (){
                    testResult.done()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).onerror(function (e){
            testResult.fail(e.message)
        }).run()
    }
});


pkcs7TestSuit.AddTest({
    name: "p7签名(不含原文)",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq()
        seq.then(function (){
            m.signMessage({
                data:"12345678",
                detachData: true,
                success:function (message){
                    seq.resolve(message)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,message){
            m.getMessageInfo({
                message: message,
                success:function (info){
                    Assert.AssertEqualAsyn(testResult,"", info.content,"p7content")
                    Assert.AssertAsyn(testResult, info.signCert.length>0,"p7signCert")
                    Assert.AssertAsyn(testResult, info.timeStamp.length===0,"p7timeStamp")
                    seq.resolve(message)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,message){
            m.verifyMessage({
                data:"12345678",
                message: message,
                success:function (){
                    testResult.done()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).onerror(function (e){
            testResult.fail(e.message)
        }).run()
    }
});

pkcs7TestSuit.AddTest({
    name: "p7签名(含时间戳)",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq()
        var timeStamp=""
        seq.then(function (){
            m.signMessage({
                data:"12345678",
                withTimeStamp: true,
                success:function (message){
                    seq.resolve(message)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,message){
            m.getMessageInfo({
                message: message,
                success:function (info){
                    Assert.AssertEqualAsyn(testResult,"12345678", info.content,"p7content")
                    Assert.AssertAsyn(testResult, info.signCert.length>0,"p7signCert")
                    Assert.AssertAsyn(testResult, info.timeStamp.length>0,"p7timeStamp")
                    timeStamp=info.timeStamp
                    seq.resolve(message)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,message){
            m.verifyMessage({
                message: message,
                success:function (){
                    seq.resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (){
            m.verifyTimeStamp({
                data:"12345678",
                timeStamp:timeStamp,
                success:function (){
                    testResult.done()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).onerror(function (e){
            testResult.fail(e.message)
        }).run()
    }
});

function p7Encrypt(testResult,alg,useDevice){
    var seq=new HebcaMiddleware.Seq()
    seq.then(function (){
        m.getCerts({
            certType:"crypt",
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
    }).then(function (seq,cert){
        m.envelopeMessage({
            data:"12345678",
            certs: [cert],
            encryptUseDevice: useDevice,
            alg: alg,
            success:function (message){
                seq.resolve(message)
            },
            fail:function (e){
                seq.reject(e)
            }
        })
    }).then(function (seq,message){
        m.unenvelopeMessage({
            message: message,
            encryptUseDevice: useDevice,
            success:function (data){
                Assert.AssertEqualAsyn(testResult,"12345678", data,"p7data")
                testResult.done()
            },
            fail:function (e){
                seq.reject(e)
            }
        })
    }).onerror(function (e){
        testResult.fail(e.message)
    }).run()
}


pkcs7TestSuit.AddTest({
    name: "p7加密解密(DES_CBC软算法)",
    asyn: true,
    fun: function (testResult) {
        p7Encrypt(testResult,"DES_CBC",false)
    }
});

pkcs7TestSuit.AddTest({
    name: "p7加密解密(DES3_CBC软算法)",
    asyn: true,
    fun: function (testResult) {
        p7Encrypt(testResult,"DES3_CBC",false)
    }
});

pkcs7TestSuit.AddTest({
    name: "p7加密解密(AES_CBC软算法)",
    asyn: true,
    fun: function (testResult) {
        p7Encrypt(testResult,"AES_CBC",false)
    }
});

pkcs7TestSuit.AddTest({
    name: "p7加密解密(SM4_CBC软算法)",
    asyn: true,
    fun: function (testResult) {
        p7Encrypt(testResult,"SM4_CBC",false)
    }
});

pkcs7TestSuit.AddTest({
    name: "p7加密解密(SM4_CBC硬算法)",
    asyn: true,
    fun: function (testResult) {
        p7Encrypt(testResult,"SM4_CBC",true)
    }
});

pkcs7TestSuit.AddTest({
    name: "p7加密解密(SM1_CBC硬算法)",
    asyn: true,
    fun: function (testResult) {
        p7Encrypt(testResult,"SM1_CBC",true)
    }
});


allTestes.AddTestSuit(pkcs7TestSuit);
