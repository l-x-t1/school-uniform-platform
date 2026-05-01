var certManageTestSuit = new TestSuit("<span style='color: red'>设备管理接口(将格式化Key)</span>");

certManageTestSuit.AddTest({
    name: "格式化",
    asyn: true,
    fun: function(testResult){
        var seq=new HebcaMiddleware.Seq();
        var deviceId=null;
        seq.then(function (){
            m.selectDevice({
                success:function (device){
                    seq.resolve(device)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,device){
            deviceId=device.deviceId
            m.authBegin({
                requestId: "1343242342341234",
                deviceId: device.deviceId,
                success: function (authRequest){
                    seq.resolve(authRequest)
                },
                fail: function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,authRequest){
            $.ajax({
                type: "POST",
                url: demoServer+'/demo/auth',
                data: "authRequest="+authRequest,
                success: function (resp) {
                    if(resp.code===0){
                        seq.resolve(resp.data)
                    }else{
                        seq.reject(new Error("服务端返回错误:"+resp.code))
                    }
                },
                error: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function (seq,authResponse){
            m.format({
                authResponse: authResponse,
                deviceId:deviceId,
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

certManageTestSuit.AddTest({
    name: "新办",
    asyn: true,
    fun: function(testResult){
        var seq=new HebcaMiddleware.Seq()
        seq.then(function (){
            m.fireEvent({
                name: "install-cert-begin"
            })
            m.genKeyPair({
                password:"123456",
                success:function (pubKey){
                    seq.resolve(pubKey)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,pubKey){
            $.ajax({
                type: "POST",
                url: demoServer+'/demo/newCert',
                data: "pubKey="+pubKey,
                success: function (resp) {
                    if(resp.code===0){
                        seq.resolve(resp)
                    }else{
                        seq.reject(new Error("服务端返回错误:"+resp.code))
                    }
                },
                error: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function (seq,data){
            m.importKeyPair({
                encryptKeyPair: data.encryptPriKey,
                success:function (){
                    seq.resolve(data)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,data){
            m.importCert({
                cert: data.signCert,
                isSign:true,
                success:function (){
                    seq.resolve(data)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,data){
            m.importCert({
                cert: data.cryptCert,
                isSign:false,
                success:function (){
                    m.fireEvent({
                        name: "install-cert-end",
                        object:JSON.stringify({
                            success: true,
                            cn: "testSm2"
                        })
                    })
                    testResult.done()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).onerror(function (e){
            m.fireEvent({
                name: "install-cert-end",
                object:JSON.stringify({
                    success: false,
                    message: e.message
                })
            })
            testResult.fail(e.message)
        }).run()
    }
});

certManageTestSuit.AddTest({
    name: "新证书签名和验证",
    asyn: true,
    fun: function(testResult){
        var seq=new HebcaMiddleware.Seq()
        seq.then(function (){
            m.sign({
                data:"123456",
                success:function (ret){
                    seq.resolve(ret)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,ret){
            m.verifySign({
                data:"123456",
                cert: ret.cert,
                signature: ret.signature,
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


certManageTestSuit.AddTest({
    name: "新证书非对称加密和解密",
    asyn: true,
    fun: function(testResult){
        var seq=new HebcaMiddleware.Seq()
        seq.then(function (){
            m.selectCert({
                certType: "crypt",
                success:function (cert){
                    seq.resolve(cert)
                },
                fail: function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,cert){
            m.certEncrypt({
                data:"123456",
                cert: cert.cert,
                success:function (ret){
                    seq.resolve(ret)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,ret){
            m.certDecrypt({
                encryptData:ret,
                success:function (data){
                    Assert.AssertEqualAsyn(testResult,"123456",data,"非对称解密")
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



allTestes.AddTestSuit(certManageTestSuit);
