var performTestSuit = new TestSuit("硬件对称加密性能");

function genAndWrapperAndImprotAndCryptFilePerform(testResult,cert,alg,iv,padding,file,fileSize,resovle,reject){
    var keysGen=null;
    var data_encrypt=null;
    var seq=new HebcaMiddleware.Seq();
    var startTime;
    var endTime;
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
        startTime=new Date()
        m.encryptFile({
            keyId: keys.keyId,
            file: fileRoot+"files/"+file,
            encryptFile: fileRoot+"files/"+file+".encrypt",
            iv:HebcaMiddleware.base64.encode(iv),
            padding:padding,
            success:function () {
                endTime=new Date()
                var ms=endTime - startTime;   // 毫秒数
                Trace.TraceLn(alg+"加密速度："+(fileSize/(ms/1000))+"字节/秒")
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
        startTime=new Date()
        m.decryptFile({
            keyId:keyId,
            file: fileRoot+"files/"+file+".decrypt",
            encryptFile: fileRoot+"files/"+file+".encrypt",
            iv:HebcaMiddleware.base64.encode(iv),
            padding:padding,
            success:function (data) {
                endTime=new Date()
                var ms=endTime - startTime;   // 毫秒数
                Trace.TraceLn(alg+"解密速度："+(fileSize/(ms/1000))+"字节/秒")
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

performTestSuit.AddTest({
    name: "硬件对称加密和解密-SM4_CBC-56K文件",
    asyn: true,
    fun: function (testResult) {
        forEachCryptCert(testResult,function (cert,resovle,reject) {
            genAndWrapperAndImprotAndCryptFilePerform(testResult,cert,"SM4_CBC","1234567812345678","pkcs5","1.dat",56884,resovle,reject);
        });
    }
});

performTestSuit.AddTest({
    name: "硬件对称加密和解密-SM1_CBC-56K文件",
    asyn: true,
    fun: function (testResult) {
        forEachCryptCert(testResult,function (cert,resovle,reject) {
            genAndWrapperAndImprotAndCryptFilePerform(testResult,cert,"SM1_CBC","1234567812345678","pkcs5","1.dat",56884,resovle,reject);
        });
    }
});

performTestSuit.AddTest({
    name: "硬件对称加密和解密-SM4_CBC-49M文件",
    asyn: true,
    fun: function (testResult) {
        forEachCryptCert(testResult,function (cert,resovle,reject) {
            genAndWrapperAndImprotAndCryptFilePerform(testResult,cert,"SM4_CBC","1234567812345678","pkcs5","1.zip",49030345,resovle,reject);
        });
    }
});

performTestSuit.AddTest({
    name: "硬件对称加密和解密-SM1_CBC-49M文件",
    asyn: true,
    fun: function (testResult) {
        forEachCryptCert(testResult,function (cert,resovle,reject) {
            genAndWrapperAndImprotAndCryptFilePerform(testResult,cert,"SM1_CBC","1234567812345678","pkcs5","1.zip",49030345,resovle,reject);
        });
    }
});


allTestes.AddTestSuit(performTestSuit);