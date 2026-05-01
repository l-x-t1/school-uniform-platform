
var fileSealContext={}

function test_createFileSealContext(testResult,name){
    new HebcaMiddleware.FileSeal({
        context: name,
        success:function (s){
            fileSealContext[name]=s
            testResult.done()
        },
        fail:function (e){
            testResult.fail(e.message)
        }
    });
}


function test_verifyPdfUrl(name,fileUrl,success,fail){
    fileSealContext[name].verifyPdf({
        inUrl: fileUrl,
        detail:true,
        success:function (resp){
            if(resp.sealCount===0){
                fail(new Error("验证时没有找到印章"))
            }else{
                Trace.Info("验证时PDF中签章数量:"+resp.sealCount)
                for(var i=0;i<resp.results.length;i++){
                    if(resp.results[i].code!==0){
                        if(resp.results[i].message){
                            fail(new Error("验证第"+i+"个签章失败："+resp.results[i].message))
                        }else{
                            fail(new Error("验证第"+i+"个签章失败：code="+resp.results[i].code))
                        }

                        return
                    }else{
                        Trace.Info("签章"+i+" - signTime:"+resp.results[i].info.signTime)
                        if(resp.results[i].info.sealInfo){
                            Trace.Info("签章"+i+" - sealInfo:"+resp.results[i].info.sealInfo.sealName)
                        }
                    }
                }
                success()
            }
        },
        fail:function (e){
            fail(e)
        }
    })
}

function test_verifyPdf(name,file,success,fail){
    fileSealContext[name].verifyPdf({
        inFile: file,
        detail:true,
        success:function (resp){
            if(resp.sealCount===0){
                fail(new Error("验证时没有找到印章"))
            }else{
                Trace.Info("验证时PDF中签章数量:"+resp.sealCount)
                for(var i=0;i<resp.results.length;i++){
                    if(resp.results[i].code!==0){
                        if(resp.results[i].message){
                            fail(new Error("验证第"+i+"个签章失败："+resp.results[i].message))
                        }else{
                            fail(new Error("验证第"+i+"个签章失败：code="+resp.results[i].code))
                        }
                        return
                    } else {
                        Trace.Info("签章"+i+" - signTime:"+resp.results[i].info.signTime)
                        if(resp.results[i].info.sealInfo){
                            Trace.Info("签章"+i+" - sealInfo:"+resp.results[i].info.sealInfo.sealName)
                        }
                    }
                }
                success()
            }
        },
        fail:function (e){
            fail(e)
        }
    })
}

function test_pfdSignSealHash(testResult,name,param,fileSrc){
    forEachSeal(testResult,name,function (seal,resolve,reject) {
        var pdfFileSrc = "1.pdf"
        var pdfFileDest = "2.pdf"
        var sealSn;
        var sealB64;
        var certB64;
        var hash;
        var sealHashResult;
        var s;

        if(fileSrc){
            pdfFileSrc=fileSrc
        }

        var seq = new HebcaMiddleware.Seq();
        seq.then(function () {
            sealContext[name].getSealB64({
                sealSn: seal.sealSn,
                success: function (sealB64Data) {
                    sealB64 = sealB64Data;
                    seq.resolve()
                },
                fail: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function () {
            m.getCertInfo({
                sn: seal.certSn,
                success: function (cert) {
                    certB64 = cert.cert;
                    seq.resolve()
                },
                fail: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function () {
            $.ajax({
                type: "POST",
                url: demoServer + '/demo/pdfHash',
                data: "pdfFile=" + pdfFileSrc + "&seal=" + sealB64 + "&cert=" + certB64 + param,
                success: function (resp) {
                    if (resp.ret === 0) {
                        hash = resp.hash
                        seq.resolve()
                    } else {
                        seq.reject(new Error("服务端返回错误:" + resp.message))
                    }
                },
                error: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function () {
            sealContext[name].signSealHash({
                sealSn: seal.sealSn,
                hash: hash,
                success: function (sealSignature) {
                    sealHashResult = sealSignature
                    seq.resolve()
                },
                fail: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function () {
            $.ajax({
                type: "POST",
                url: demoServer + '/demo/pdfMerge',
                data: "pdfFile=" + pdfFileDest + "&sealSignature=" + sealHashResult,
                success: function (resp) {
                    if (resp.ret === 0) {
                        seq.resolve()
                    } else {
                        seq.reject(new Error("服务端返回错误:" + resp.message))
                    }
                },
                error: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function () {
            test_verifyPdfUrl(name, demoServer + "/demo/pdf/" + pdfFileDest, function () {
                resolve()
            }, function (e) {
                seq.reject(e)
            })
        }).onerror(function (e) {
            reject(e)
        }).run()
    })
}

function test_pfdSignSealHashQP(testResult,name,param,fileSrc){
    forEachSignCert(testResult,function (cert,resolve,reject) {
        var pdfFileSrc = "1.pdf"
        var pdfFileDest = "2.pdf"
        var hash;
        var sealHashResult;
        var s;

        if (fileSrc) {
            pdfFileSrc = fileSrc
        }

        var certB64=cert.cert;
        var certId=cert.certId;
        var signText = "你好你好"
        var signImage = "1.png"

        var seq = new HebcaMiddleware.Seq();
        seq.then(function () {
            $.ajax({
                type: "POST",
                url: demoServer + '/demo/pdfHash',
                data: "pdfFile=" + pdfFileSrc + "&cert=" + certB64 + "&certId=" + certId + "&signText=" + signText + "&signPic=" + signImage + param,
                success: function (resp) {
                    if (resp.ret === 0) {
                        hash = resp.hash
                        seq.resolve()
                    } else {
                        seq.reject(new Error("服务端返回错误:" + resp.message))
                    }
                },
                error: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function () {
            m.getCertInfo({
                cert: certB64,
                success: function (c) {
                    seq.resolve(c)
                },
                fail: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function (seq, c) {
            var hashParts = hash.split("|")   //格式 hash|toBeSign

            m.sign({
                sn: c.sn,
                dataFormat: "Base64",
                data: hashParts[1],
                success: function (signature) {
                    sealHashResult = hashParts[0] + "|" + signature.signature
                    seq.resolve()
                },
                fail: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function () {
            $.ajax({
                type: "POST",
                url: demoServer + '/demo/pdfMerge',
                data: "pdfFile=" + pdfFileDest + "&sealSignature=" + sealHashResult,
                success: function (resp) {
                    if (resp.ret === 0) {
                        seq.resolve()
                    } else {
                        seq.reject(new Error("服务端返回错误:" + resp.message))
                    }
                },
                error: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function (){
            test_verifyPdfUrl(name, demoServer + "/demo/pdf/" + pdfFileDest, function () {
                resolve()
            }, function (e) {
                seq.reject(e)
            })
        }).onerror(function (e) {
            reject(e)
        }).run()
    })
}

function test_signClientFile(testResult,name,location,sealType,enableTimeStamp,fileSrc){
    var qpImage=fileRoot+pathJoin("files","1.png")
    var pdfFileSrc = fileRoot+pathJoin("files","1.pdf")
    var pdfFileDest = fileRoot+pathJoin("files","2.pdf")
    if(fileSrc){
        pdfFileSrc=fileRoot+pathJoin("files",fileSrc)
    }

    forEachSeal(testResult,name,function (seal,resolve,reject) {

        var seq = new HebcaMiddleware.Seq();
        seq.then(function () {
            if (sealType === 2) {
                m.readFile({
                    file: qpImage,
                    dataFormat: "Base64",
                    success: function (data) {
                        seq.resolve(data)
                    },
                    fail: function (e) {
                        seq.resolve(e)
                    }
                })
            } else {
                seq.resolve("")
            }
        }).then(function (seq, picData) {
            fileSealContext[name].signPdf({
                inFile: pdfFileSrc,
                outFile: pdfFileDest,
                command: {
                    sealType: sealType,
                    sealSn: seal.sealSn,
                    multiShow: true,
                    location: location,
                    sign: {
                        text: "你好你好",
                        pic: picData,
                        picDPI: 200
                    }
                },
                enableTimeStamp: enableTimeStamp,
                success: function () {
                    seq.resolve()
                },
                fail: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function (){
            test_verifyPdf(name, pdfFileDest, function () {
                resolve()
            }, function (e) {
                seq.reject(e)
            })
        }).onerror(function (e) {
            reject(e)
        }).run()
    })
}

function test_signClientFileQP(testResult,name,location,sealType,enableTimeStamp,fileSrc){
    var qpImage=fileRoot+pathJoin("files","1.png")
    var pdfFileSrc = fileRoot+pathJoin("files","1.pdf")
    var pdfFileDest = fileRoot+pathJoin("files","2.pdf")
    if(fileSrc){
        pdfFileSrc=fileRoot+pathJoin("files",fileSrc)
    }

    forEachSignCert(testResult,function (cert,resolve,reject) {

        var seq = new HebcaMiddleware.Seq();
        seq.then(function () {
            if (sealType === 2) {
                m.readFile({
                    file: qpImage,
                    dataFormat: "Base64",
                    success: function (data) {
                        seq.resolve(data)
                    },
                    fail: function (e) {
                        seq.resolve(e)
                    }
                })
            } else {
                seq.resolve("")
            }
        }).then(function (seq, picData) {
            fileSealContext[name].signPdf({
                inFile: pdfFileSrc,
                outFile: pdfFileDest,
                command: {
                    sealType: sealType,
                    location: location,
                    multiShow: true,
                    sign: {
                        certSubjectId: cert.certId,
                        text: "你好你好",
                        pic: picData,
                        picDPI: 200
                    }
                },
                enableTimeStamp: enableTimeStamp,
                success: function () {
                    seq.resolve()
                },
                fail: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function (){
            test_verifyPdf(name, pdfFileDest, function () {
                resolve()
            }, function (e) {
                seq.reject(e)
            })
        }).onerror(function (e) {
            reject(e)
        }).run()
    })
}

function test_pdfSignSealHashFx(testResult,name,command,sealType,enableTimeStamp,fileSrc){
    var qpImage=fileRoot+pathJoin("files","1.png")
    var pdfFileSrc = fileRoot+pathJoin("files","1.pdf")
    var pdfFileDest = fileRoot+pathJoin("files","2.pdf")
    var signText = "你好你好"
    if(fileSrc){
        pdfFileSrc=fileRoot+pathJoin("files",fileSrc)
    }

    var seq=new HebcaMiddleware.Seq();
    var certSubjectId=null;
    var dataHash=null;
    command.sealType=sealType
    command.multiShow=true
    var imagePic=""
    seq.then(function (){
        if(sealType===2){
            m.readFile({
                file: qpImage,
                dataFormat: "Base64",
                success:function (data) {
                    imagePic=data
                    seq.resolve()
                },
                fail:function (e) {
                    seq.resolve(e)
                }
            })
        }else{
            seq.resolve("")
        }
    }).then(function (){
        if(sealType===0){
            //从服务端获取印章
            $.ajax({
                type: "POST",
                url: demoServer+'/demo/getSeal',
                data: "sealSn="+sealSnInCloud,
                success: function (resp) {
                    if(resp.ret===0){
                        seq.resolve(resp)
                    }else{
                        seq.reject(new Error(resp.message))
                    }
                },
                error: function (e) {
                    seq.reject(e)
                }
            })
        }else{
            //从服务端获取证书
            $.ajax({
                type: "POST",
                url: demoServer+'/demo/getCert',
                data: "certSubjectId="+certIdInCloud,
                success: function (resp) {
                    if(resp.ret===0){
                        seq.resolve(resp)
                    }else{
                        seq.reject(new Error(resp.message))
                    }
                },
                error: function (e) {
                    seq.reject(e)
                }
            })
        }
    }).then(function (seq, info){
        if(sealType===0){
            command.seal=info.sealB64
            certSubjectId=info.certSubjectId
            command.signCert=info.certB64
            //客户端计算摘要
            fileSealContext[name].hashPdf({
                inFile: pdfFileSrc,
                command: command,
                enableTimeStamp: enableTimeStamp,
                success: function (data) {
                    seq.resolve(data)
                },
                fail: function (e) {
                    seq.reject(e)
                }
            })
        }else{
            command.sign={
                pic:imagePic,
                text: signText,
                certSubjectId: certIdInCloud,
                picDPI: 200,
            }
            certSubjectId=certIdInCloud
            command.signCert=info.certB64
            //客户端计算摘要
            fileSealContext[name].hashPdf({
                inFile: pdfFileSrc,
                command: command,
                enableTimeStamp:enableTimeStamp,
                success: function (data) {
                    seq.resolve(data)
                },
                fail: function (e) {
                    seq.reject(e)
                }
            })
        }
    }).then(function (seq, data){
        //服务端签名
        hashParts=data.split(";")
        $.ajax({
            type: "POST",
            url: demoServer+'/demo/certSign',
            data: "certSubjectId="+certSubjectId+"&dataB64="+hashParts[1]+"&password="+certPasswordInCloud,
            success: function (resp) {
                if(resp.ret===0){
                    seq.resolve(hashParts[0]+";"+resp.signature)
                }else{
                    seq.reject(new Error(resp.message))
                }
            },
            error: function (e) {
                seq.reject(e)
            }
        })
    }).then(function (seq,signature){
        //客户端合并
        fileSealContext[name].mergePdf({
            outFile: pdfFileDest,
            signature: signature,
            dataHash: dataHash,
            enableTimeStamp:enableTimeStamp,
            success: function () {
                seq.resolve()
            },
            fail: function (e) {
                seq.reject(e)
            }
        })
    }).then(function (){
        test_verifyPdf(name, pdfFileDest, function () {
            testResult.done()
        }, function (e) {
            seq.reject(e)
        })
    }).onerror(function (e){
        testResult.fail(e.message);
    }).run();
}
