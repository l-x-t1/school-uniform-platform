var epsTestSuit = new TestSuit("<span style='color: red'>国办公安接口(将覆盖国办印章数据)</span><br>");

epsTestSuit.AddTest({
    name: "写入公安密钥和数据",
    asyn: true,
    fun: function(testResult){
        var seq=new HebcaMiddleware.Seq();
        var cryptCert=null;
        var appKey=null;
        var dataKey=null;
        var gaData=null;
        seq.then(function (){
            m.selectCert({
                certType:"crypt",
                success:function (cert){
                    cryptCert=cert.cert
                    seq.resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (){
            m.getRandom({
                randomLength: 16,
                success:function (r1){
                    seq.resolve(r1)
                },
                fail:function (e){
                    seq.resolve(e)
                }
            })
        }).then(function (seq,r1){
            m.certEncrypt({
                data: r1,
                dataFormat: 'base64',
                cert:cryptCert,
                success:function (en1){
                    appKey=en1
                    seq.resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,d){
            m.getRandom({
                randomLength: 16,
                success:function (r2){
                    seq.resolve(r2)
                },
                fail:function (e){
                    seq.resolve(e)
                }
            })
        }).then(function (seq,r2){
            m.certEncrypt({
                data: r2,
                dataFormat: 'base64',
                cert: cryptCert,
                success:function (en2){
                    dataKey=en2
                    seq.resolve(r2)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,r2){
            m.encryptSoft({
                key: r2,
                alg: "SM4_ECB",
                data: "1234567812345678",
                success:function (en){
                    gaData=en
                    seq.resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (){
            m.epsImportSymmKey({
                keyIndex: 0xA2,
                key: appKey,
                success:function (){
                    seq.resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (){
            m.epsImportSymmKey({
                keyIndex: 0xA6,
                key: dataKey,
                success:function (){
                    seq.resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (){
            m.epsWriteESealData({
                sealData: gaData,
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

epsTestSuit.AddTest({
    name: "读取公安数据",
    asyn: true,
    fun: function(testResult){
        m.epsReadESealData({
            keyIndex:0xA6,
            alg: "SM4_ECB",
            success:function (data){
                Assert.AssertEqualAsyn(testResult,"1234567812345678",HebcaMiddleware.base64.decode(data),"读取公安数据")
                testResult.done()
            },
            fail:function (e){
                testResult.fail(e.message)
            }
        })
    }
});

epsTestSuit.AddTest({
    name: "加密和解密",
    asyn: true,
    fun: function(testResult){
        var seq=new HebcaMiddleware.Seq();
        seq.then(function (){
            m.epsEncrypt({
                keyIndex:0xA6,
                alg: "SM4_ECB",
                inputData: HebcaMiddleware.base64.encode("1234567812345678"),
                success:function (data){
                    seq.resolve(data)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,encryptData){
            m.epsDecrypt({
                keyIndex:0xA6,
                alg: "SM4_ECB",
                inputData: encryptData,
                success:function (data){
                    Assert.AssertEqualAsyn(testResult,"1234567812345678", HebcaMiddleware.base64.decode(data),"解密")
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


allTestes.AddTestSuit(epsTestSuit);

