checkEndsWith()

function test_verifyOfdUrl(name,fileUrl,success,fail){
    fileSealContext[name].verifyOfd({
        inUrl: fileUrl,
        detail:true,
        success:function (resp){
            if(resp.sealCount===0){
                fail(new Error("验证时没有找到印章"))
            }else{
                Trace.Info("验证时OFD中签章数量:"+resp.sealCount)
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

function test_verifyOfd(name,file,success,fail){
    fileSealContext[name].verifyOfd({
        inFile: file,
        detail:true,
        success:function (resp){
            if(resp.sealCount===0){
                fail(new Error("验证时没有找到印章"))
            }else{
                Trace.Info("验证时Ofd中签章数量:"+resp.sealCount)
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

function test_ofdSignSealHash(testResult,name,param,sealType,fileSrc){
    forEachSeal(testResult,name,function (seal,resolve,reject) {

        var ofdFileSrc = "1.ofd"
        var ofdFileDest = "2.ofd"
        var sealSn=seal.sealSn;
        var sealB64;
        var certSn=seal.certSn;
        var certB64;
        var hash;
        var sealHashResult;
        var s;

        if (fileSrc) {
            ofdFileDest = fileSrc
        }

        var seq = new HebcaMiddleware.Seq();
        seq.then(function () {
            sealContext[name].getSealB64({
                sealSn: seal.sealSn,
                success: function (seal) {
                    sealB64 = seal;
                    seq.resolve()
                },
                fail: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function (){
            //判读印象，如果是自定义格式，则忽略该印章
            sealContext[name].getSealInfo({
                seal: sealB64,
                success: function (sealInfo) {
                    if(sealInfo.formatType==="TX"){
                        //自定义格式，忽略
                        Trace.Warning("忽略自定义格式印章："+sealInfo.sealName)
                        resolve()
                    }else if(sealInfo.formatType==="GM" && seal.context.endsWith("net")){
                        //忽略网络版的国密格式印章，因为其包含扩展项，ofd签章不支持
                        Trace.Warning("忽略网络版国密格式印章："+sealInfo.sealName)
                        resolve()
                    }else{
                        //继续
                        seq.resolve()
                    }
                },
                fail: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function () {
            $.ajax({
                type: "POST",
                url: demoServer + '/demo/ofdHash',
                data: "ofdFile=" + ofdFileSrc + "&seal=" + sealB64  + param,
                success: function (resp) {
                    if (resp.ret === 0) {
                        hash = resp.hash
                        seq.resolve()
                    } else {
                        seq.reject(new Error("服务端返回错误:" + resp.message))
                    }
                },
                error: function (e) {
                    seq.reject(new Error(e.responseJSON.message))
                }
            })
        }).then(function () {
            m.sign({
                sn: certSn,
                data: hash,
                dataFormat: "Base64",
                success: function (signature) {
                    sealHashResult = signature.signature
                    seq.resolve()
                },
                fail: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function () {
            $.ajax({
                type: "POST",
                url: demoServer + '/demo/ofdMerge',
                data: "ofdFile=" + ofdFileDest + "&signature=" + sealHashResult + "&hash=" + hash,
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
            test_verifyOfdUrl(name, demoServer + "/demo/ofd/" + ofdFileDest, function () {
                resolve()
            }, function (e) {
                seq.reject(e)
            })
        }).onerror(function (e) {
            reject(e)
        }).run()
    })
}


function test_ofdSignSealHashQP(testResult,name,param,sealType,fileSrc){
    forEachSignCert(testResult,function (cert,resolve,reject) {
        var ofdFileSrc = "1.ofd"
        var ofdFileDest = "2.ofd"
        var certSn=cert.sn;
        var certB64=cert.cert;
        var hash;
        var sealHashResult;
        var s;
        var signText = "你好你好"
        var signImage = "1.png"

        if (fileSrc) {
            ofdFileSrc = fileSrc
        }

        var seq = new HebcaMiddleware.Seq();
        seq.then(function () {
            $.ajax({
                type: "POST",
                url: demoServer + '/demo/ofdHash',
                data: "ofdFile=" + ofdFileSrc + "&signText=" + signText + "&signPic=" + signImage + "&cert=" + certB64 + param,
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
            m.sign({
                sn: certSn,
                data: hash,
                dataFormat: "Base64",
                success: function (signature) {
                    sealHashResult = signature.signature
                    seq.resolve()
                },
                fail: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function () {
            $.ajax({
                type: "POST",
                url: demoServer + '/demo/ofdMerge',
                data: "ofdFile=" + ofdFileDest + "&signature=" + sealHashResult + "&hash=" + hash,
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
            test_verifyOfdUrl(name, demoServer + "/demo/ofd/" + ofdFileDest, function () {
                resolve()
            }, function (e) {
                seq.reject(e)
            })
        }).onerror(function (e) {
            alert(e.message)
        }).run()
    })
}


function test_ofdSignSealHashOes(testResult,name,param,fileSrc){
    forEachSeal(testResult,name,function (seal,resolve,reject) {
        var ofdFileSrc = "1.ofd"
        var ofdFileDest = "2.ofd"
        var sealSn=seal.sealSn;
        var sealB64;
        var certSn=seal.certSn;
        var certB64;
        var hash;
        var sealHashResult;
        var s;
        var oesPlugin=seal.plugin;
        var oesSealWidth;
        var oesSealHeight;
        var oesProviderName;
        var oesProviderCompany;
        var oesProviderVersion;
        var oesDocProperty;

        if(fileSrc){
            ofdFileDest=fileSrc
        }

        var seq=new HebcaMiddleware.Seq();
        seq.then(function (){
            sealContext[name].getSealB64({
                sealSn:seal.sealSn,
                success:function (seal) {
                    sealB64=seal;
                    seq.resolve()
                },
                fail:function (e) {
                    //无法获取印章，可能由于印章为自定义格式，忽略
                    Trace.Warning("忽略印章:"+seal.sealName+" ,无法获取印章："+e.message)
                    resolve()

                }
            })
        }).then(function (){
            //判读印象，如果是自定义格式，则忽略该印章
            sealContext[name].getSealInfo({
                seal: sealB64,
                success: function (sealInfo) {
                    if(sealInfo.formatType==="TX"){
                        //自定义格式，忽略
                        Trace.Warning("忽略自定义格式印章："+sealInfo.sealName)
                        resolve()
                    }else if(sealInfo.formatType==="GM" && seal.context.endsWith("net")){
                        //忽略网络版的国密格式印章，因为其包含扩展项，ofd签章不支持
                        Trace.Warning("忽略网络版国密格式印章："+sealInfo.sealName)
                        resolve()
                    }else if(sealInfo.formatType==="GB" ){
                        //河北CAOES插件国办格式对时间戳处理有问题，忽略
                        Trace.Warning("忽略国办格式印章："+sealInfo.sealName)
                        resolve()
                    }else{
                        //继续
                        seq.resolve()
                    }
                },
                fail: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function () {
            sealContext[name].oesGetSealImageById({
                plugin: oesPlugin,
                sealId: sealSn,
                success:function (info) {
                    oesSealWidth=info.width
                    oesSealHeight=info.height
                    seq.resolve()
                },
                fail:function (e) {
                    seq.reject(e)
                }
            })
        }).then(function (){
            sealContext[name].oesGetProviderInfo({
                plugin: oesPlugin,
                success:function (info) {
                    oesProviderName=info.name
                    oesProviderCompany=info.company
                    oesProviderVersion=info.version
                    seq.resolve()
                },
                fail:function (e) {
                    seq.reject(e)
                }
            })
        }).then(function(){
            $.ajax({
                type: "POST",
                url: demoServer+'/demo/ofdHashOes',
                data: "ofdFile="+ofdFileSrc+"&seal="+sealB64+"&sealHeight="+oesSealHeight+"&sealWidth="+oesSealWidth+
                    "&providerName="+oesProviderName+"&providerComanpy="+oesProviderCompany+"&providerVersion="+oesProviderVersion+param,
                success: function (resp) {
                    if(resp.ret===0){
                        hash=resp.hash
                        oesDocProperty=resp.docProperty
                        seq.resolve()
                    }else{
                        seq.reject(new Error("服务端返回错误:"+resp.message))
                    }
                },
                error: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function (){
            sealContext[name].signSealHash({
                sealSn:sealSn,
                hash: hash,
                docProperty: oesDocProperty,
                success:function (signature) {
                    sealHashResult=signature
                    seq.resolve()
                },
                fail:function (e) {
                    seq.reject(e)
                }
            })
        }).then(function (){
            $.ajax({
                type: "POST",
                url: demoServer+'/demo/ofdMergeOes',
                data: "ofdFile="+ofdFileDest +"&signature="+sealHashResult+"&hash="+hash,
                success: function (resp) {
                    if(resp.ret===0){
                        seq.resolve()
                    }else{
                        seq.reject(new Error("服务端返回错误:"+resp.message))
                    }
                },
                error: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function (){
            test_verifyOfdUrl(name, demoServer + "/demo/ofd/" + ofdFileDest, function () {
                resolve()
            }, function (e) {
                seq.reject(e)
            })
        }).onerror(function (e){
            reject(e)
        }).run()
    })

}

function test_ofdSignClientFile(testResult,name,location,sealType,qpType,enableTimeStamp){
    var qpImage=fileRoot+pathJoin("files","1.png")
    var ofdFileSrc = fileRoot+pathJoin("files","1.ofd")
    var ofdFileDest = fileRoot+pathJoin("files","2.ofd")
    var sealB64;

    forEachSeal(testResult,name,function (seal,resolve,reject) {
        var seq = new HebcaMiddleware.Seq();
        seq.then(function (){
            sealContext[name].getSealB64({
                sealSn:seal.sealSn,
                success:function (seal) {
                    sealB64=seal;
                    seq.resolve()
                },
                fail:function (e) {
                    //无法获取印章，可能由于印章为自定义格式，忽略
                    Trace.Warning("忽略印章:"+seal.sealName+" ,无法获取印章："+e.message)
                    resolve()
                }
            })
        }).then(function (){
            //判读印象，如果是自定义格式，则忽略该印章
            sealContext[name].getSealInfo({
                seal: sealB64,
                success: function (sealInfo) {
                    if(sealInfo.formatType==="TX"){
                        //自定义格式，忽略
                        Trace.Warning("忽略自定义格式印章："+sealInfo.sealName)
                        resolve()
                    }else if(sealInfo.formatType==="GM" && seal.context.endsWith("net")){
                        //忽略网络版的国密格式印章，因为其包含扩展项，ofd签章不支持
                        Trace.Warning("忽略网络版国密格式印章："+sealInfo.sealName)
                        resolve()
                    }else if(sealInfo.formatType==="GB" ){
                        //河北CAOES插件国办格式对时间戳处理有问题，忽略
                        Trace.Warning("忽略国办格式印章："+sealInfo.sealName)
                        resolve()
                    }else{
                        //继续
                        seq.resolve()
                    }
                },
                fail: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function () {
            if (sealType === 1) {
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
            fileSealContext[name].signOfd({
                inFile: ofdFileSrc,
                outFile: ofdFileDest,
                command: {
                    sealType: sealType,   //0：签章， 1： 签批
                    multiShow: (location.type===1), //关键字盖章，要求true
                    location: location,
                    sealSn: seal.sealSn,
                    sign: {
                        type: qpType,  //0： 图片签批， 1: 文字签批
                        text: "你好你好",
                        pic: picData,
                        picDPI: 200,
                        picType: "png",
                        picInfo: {
                            borderWidth: 0,
                            borderHeight: 0,
                        }
                    }
                },
                enableTimeStamp: enableTimeStamp,
                success: function () {
                    seq.resolve()
                },
                fail: function (e) {
                    seq.reject(e);
                }
            })
        }).then(function (){
            test_verifyOfd(name, ofdFileDest, function () {
                resolve()
            }, function (e) {
                seq.reject(e)
            })
        }).onerror(function (e) {
            reject(e)
        }).run()
    })
}

function test_ofdSignClientFileQP(testResult,name,location,sealType,qpType,enableTimeStamp){
    var qpImage=fileRoot+pathJoin("files","1.png")
    var ofdFileSrc = fileRoot+pathJoin("files","1.ofd")
    var ofdFileDest = fileRoot+pathJoin("files","2.ofd")

    forEachSignCert(testResult,function (cert,resolve,reject) {
        var seq = new HebcaMiddleware.Seq();
        seq.then(function () {
            if (sealType === 1) {
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
            fileSealContext[name].signOfd({
                inFile: ofdFileSrc,
                outFile: ofdFileDest,
                command: {
                    sealType: sealType,   //0：签章， 1： 签批
                    location: location,
                    sign: {
                        certSubjectId: cert.certId,
                        type: qpType,  //0： 图片签批， 1: 文字签批
                        text: "你好你好",
                        pic: picData,
                        picDPI: 200,
                        picType: "png",
                        picInfo: {
                            borderWidth: 0,
                            borderHeight: 0,
                        }
                    }
                },
                enableTimeStamp: enableTimeStamp,
                success: function () {
                    seq.resolve()
                },
                fail: function (e) {
                    seq.reject(e);
                }
            })
        }).then(function (){
            test_verifyOfd(name, ofdFileDest, function () {
                resolve()
            }, function (e) {
                seq.reject(e)
            })
        }).onerror(function (e) {
            reject(e)
        }).run()
    })
}

function test_ofdSignSealHashFx(testResult,name,command,sealType,qpType,enableTimeStamp){
    var seq=new HebcaMiddleware.Seq();
    var certSubjectId=null;
    var dataHash=null;
    command.sealType=sealType
    var imagePic=""
    var qpImage=fileRoot+pathJoin("files","1.png")
    var ofdFileSrc = fileRoot+pathJoin("files","1.ofd")
    var ofdFileDest = fileRoot+pathJoin("files","2.ofd")
    seq.then(function (){
        if(sealType===1){
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
        if(sealType===0) {
            //从服务端获取印章
            $.ajax({
                type: "POST",
                url: demoServer + '/demo/getSeal',
                data: "sealSn=" + sealSnInCloud,
                success: function (resp) {
                    if (resp.ret === 0) {
                        seq.resolve(resp)
                    } else {
                        seq.reject(new Error(resp.message))
                    }
                },
                error: function (e) {
                    seq.reject(e)
                }
            })
        }
        else{
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
    }).then(function (seq, sealInfo){
        if(sealType===0) {
            command.seal = sealInfo.sealB64
            certSubjectId = sealInfo.certSubjectId
            //客户端计算摘要
            fileSealContext[name].hashOfd({
                inFile: ofdFileSrc,
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
        else{
            command.sign={
                type: qpType,  //0： 图片签批， 1: 文字签批
                pic:imagePic,
                text: "你好你好",
                certSubjectId: certIdInCloud,
                cert: sealInfo.certB64,
                picDPI: 200,
                picType: "png",
                picInfo: {
                    borderWidth: 0,
                    borderHeight: 0,
                }
            }
            certSubjectId=certIdInCloud
            //command.signCert=info.certB64
            //客户端计算摘要
            fileSealContext[name].hashOfd({
                inFile: ofdFileSrc,
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
        dataHash=data.dataHash
        $.ajax({
            type: "POST",
            url: demoServer+'/demo/certSign',
            data: "certSubjectId="+certSubjectId+"&dataB64="+data.data+"&password="+certPasswordInCloud,
            success: function (resp) {
                if(resp.ret===0){
                    seq.resolve(resp.signature)
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
        fileSealContext[name].mergeOfd({
            outFile: ofdFileDest,
            signature: signature,
            dataHash: dataHash,
            enableTimeStamp:enableTimeStamp,
            success: function () {
                testResult.done()
            },
            fail: function (e) {
                seq.reject(e)
            }
        })
    }).onerror(function (e){
        testResult.fail(e.message);
    }).run();
}
