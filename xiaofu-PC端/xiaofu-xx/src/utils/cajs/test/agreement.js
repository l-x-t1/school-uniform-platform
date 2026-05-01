var agreementTestSuit = new TestSuit("密钥协商接口");

function doAgreement(testResult,alg){
    var sponsorId="1234567887654321"
    var respondId="8765432112345678"
    var sponsorData=null;
    var respondData=null;
    var sponsorSn=null;
    var seq=new HebcaMiddleware.Seq();
    seq.then(function (){
        m.selectCert({
            certType:agreementCertType,
            success:function (cert){
                seq.resolve(cert)
            },
            fail:function (e){
                seq.reject(e)
            }
        })
    }).then(function (seq,cert){
        sponsorSn=cert.sn
        m.agreementBegin({
            sponsorId:sponsorId,
            sn:cert.sn,
            alg:alg,
            success:function (data){
                sponsorData=data;
                seq.resolve()
            },
            fail:function (e){
                seq.reject(e)
            }
        })
    }).then(function (){
        m.selectCert({
            certType:agreementCertType,
            success:function (cert){
                seq.resolve(cert)
            },
            fail:function (e){
                seq.reject(e)
            }
        })
    }).then(function (seq,cert){
        m.agreementResponse({
            sn: cert.sn,
            sponsorCert: sponsorData.cert,
            sponsorTempPubKey: sponsorData.tempPubkey,
            respondId: respondId,
            sponsorId: sponsorId,
            alg:alg,
            success:function (data){
                respondData=data
                seq.resolve()
            },
            fail:function (e){
                seq.reject(e)
            }
        })
    }).then(function (){
        m.agreementEnd({
            sn: sponsorSn,
            respondCert: respondData.cert,
            respondTempPubKey: respondData.tempPubkey,
            respondId: respondId,
            handleId: sponsorData.handleId,
            success:function (keyId){
                sponsorData.keyId=keyId
                seq.resolve()
            },
            fail:function (e){
                seq.reject(e)
            }
        })
    }).then(function (){
        m.encrypt({
            keyId: sponsorData.keyId,
            data: "1234567812345678",
            iv: HebcaMiddleware.base64.encode("1234567812345678"),
            padding: "pkcs5",
            alg:alg,
            success: function (encryptData){
                seq.resolve(encryptData)
            },
            fail: function (e){
                seq.reject(e)
            }
        })
    }).then(function (seq,encryptData){
        m.decrypt({
            keyId: respondData.keyId,
            encryptData: encryptData,
            iv: HebcaMiddleware.base64.encode("1234567812345678"),
            padding: "pkcs5",
            alg:alg,
            success: function (data){
                Assert.AssertEqualAsyn(testResult,"1234567812345678",data,"解密结果")
                testResult.done()
            },
            fail: function (e){
                seq.reject(e)
            }
        })
    }).onerror(function (e){
        testResult.fail(e.message)
    }).run()
}

agreementTestSuit.AddTest({
    name: "密钥协商(SM4_CBC)",
    asyn: true,
    fun: function(testResult){
        doAgreement(testResult,"SM4_CBC")
    }
});

agreementTestSuit.AddTest({
    name: "密钥协商(SM1_CBC)",
    asyn: true,
    fun: function(testResult){
        doAgreement(testResult,"SM1_CBC")
    }
});

agreementTestSuit.AddTest({
    name: "密钥协商(SM4_ECB)",
    asyn: true,
    fun: function(testResult){
        doAgreement(testResult,"SM4_ECB")
    }
});

agreementTestSuit.AddTest({
    name: "密钥协商(SM1_ECB)",
    asyn: true,
    fun: function(testResult){
        doAgreement(testResult,"SM1_ECB")
    }
});

allTestes.AddTestSuit(agreementTestSuit);
