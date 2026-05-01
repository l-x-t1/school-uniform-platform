
var sealContext={}

function test_createContext(testResult,name){
    new HebcaMiddleware.Seal({
        context: name,
        success:function (s){
            sealContext[name]=s
            testResult.done()
        },
        fail:function (e){
            testResult.fail(e.message)
        }
    });
}

function test_listSeal(testResult,name){
    sealContext[name].listSeal({
        success:function (seals) {
            Assert.AssertAsyn(testResult,seals.length>0,"没有找到印章")
            Trace.TraceLn(JSON.stringify(seals))
            testResult.done()
        },
        fail:function (e) {
            testResult.fail(e.message)
        }
    })
}

function forEachSeal(testResult,name,callback){
    sealContext[name].listSeal({
        success:function (seals) {
            var seq=new HebcaMiddleware.Seq();
            seq.thenEach(seals,function (seq,index) {
                callback(seals[index],function () {
                    seq.resolve(index+1);
                },function (e) {
                    seq.reject(e);
                });
            }).then(function () {
                testResult.done();
            }).onerror(function (e) {
                testResult.fail(e.message);
            }).run(0)
        },
        fail:function (e) {
            testResult.fail(e.message)
        }
    })
}

function firstSeal(testResult,name,callback){
    sealContext[name].listSeal({
        success:function (seals) {
            if(seals.length===0){
                testResult.fail(name+":没有找到印章")
                return
            }

            callback(seals[0],function () {
                testResult.done()
            },function (e) {
                testResult.fail(e.message)
            });
        },
        fail:function (e) {
            testResult.fail(e.message)
        }
    })
}

function test_getSealB64(testResult,name){
    forEachSeal(testResult,name,function (seal,resolve,reject){
        sealContext[name].getSealB64({
            sealSn: seal.sealSn,
            success:function (sealB64){
                Assert.AssertAsyn(testResult,sealB64.length>0,"获取印章数据")
                resolve()
            },
            fail:function (e){
                reject(e)
            }
        })
    })
}

function test_getSealInfo(testResult,name,notCheckInfo){
    forEachSeal(testResult,name,function (seal,resolve,reject){
        var seq=new HebcaMiddleware.Seq();
        seq.then(function (){
            sealContext[name].getSealInfo({
                sealSn: seal.sealSn,
                success:function (sealInfo){
                    if(!notCheckInfo){
                        Assert.AssertEqualAsyn(testResult,seal.sealSn,sealInfo.sealSn,"获取印章序列号")
                        Assert.AssertEqualAsyn(testResult,seal.sealName,sealInfo.sealName,"获取印章名称")
                    }
                    seq.resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (){
            sealContext[name].getSealB64({
                sealSn: seal.sealSn,
                success:function (sealB64){
                    seq.resolve(sealB64)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,sealB64){
            sealContext[name].getSealInfo({
                seal: sealB64,
                success:function (sealInfo){
                    if(!notCheckInfo) {
                        Assert.AssertEqualAsyn(testResult, seal.sealSn, sealInfo.sealSn, "获取印章序列号")
                        Assert.AssertEqualAsyn(testResult, seal.sealName, sealInfo.sealName, "获取印章名称")
                    }
                    resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).onerror(function (e){
            reject(e)
        }).run()
    })
}


function test_signSealSignature(testResult,name,notCheckInfo){
    forEachSeal(testResult,name,function (seal,resolve,reject){
        var seq=new HebcaMiddleware.Seq();
        seq.then(function (){
            sealContext[name].signSealSignature({
                sealSn: seal.sealSn,
                data: "123456",
                show: {
                    divId: "seal_show",
                    ui: HebcaMiddleware.uiBootstrap,
                },
                success:function (sealSignature){
                    Assert.AssertAsyn(testResult,sealSignature.length>0,"整体签章")
                    seq.resolve(sealSignature)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,sealSignature){
            sealContext[name].verifySealSignature({
                data: "123456",
                sealSignature: sealSignature,
                show: {
                    divId: "seal_show",
                    ui: HebcaMiddleware.uiBootstrap,
                },
                success:function (){
                    seq.resolve(sealSignature)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,sealSignature){
            sealContext[name].getSealSignatureInfo({
                sealSignature: sealSignature,
                success:function (info){
                    if(!notCheckInfo) {
                        Assert.AssertEqualAsyn(testResult, seal.sealSn, info.seal.sealSn, "获取印章序列号")
                        Assert.AssertEqualAsyn(testResult, seal.sealName, info.seal.sealName, "获取印章名称")
                    }
                    resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).onerror(function (e){
            reject(e)
        }).run()
    })
}

function test_signSealSignatureBatch(testResult,name,notCheckInfo){
    forEachSeal(testResult,name,function (seal,resolve,reject){
        var seq=new HebcaMiddleware.Seq();
        var times=5
        var data=[]
        for(var i=0;i<times;i++){
            data.push("123456--"+i)
        }
        var ss;
        seq.then(function (){
            sealContext[name].signSealSignatureBatch({
                sealSn: seal.sealSn,
                data: data,
                success:function (sealSignature){
                    Assert.AssertAsyn(testResult,sealSignature.length===times,"整体签章")
                    ss=sealSignature
                    seq.resolve(0)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).thenEach(data,function (seq,index){
            sealContext[name].verifySealSignature({
                data: data[index],
                sealSignature: ss[index],
                show: {
                    divId: "seal_show",
                    ui: HebcaMiddleware.uiBootstrap,
                },
                success:function (){
                    seq.resolve(index+1)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (){
            resolve()
        }).onerror(function (e){
            reject(e)
        }).run()
    })
}

function test_signSeal(testResult,name,notCheckInfo){
    forEachSeal(testResult,name,function (seal,resolve,reject){
        var seq=new HebcaMiddleware.Seq();
        seq.then(function (){
            sealContext[name].signSeal({
                sealSn: seal.sealSn,
                data: "123456",
                show: {
                    divId: "seal_show",
                    ui: HebcaMiddleware.uiBootstrap,
                },
                success:function (signData){
                    Assert.AssertAsyn(testResult,signData.cert.length>0,"分散签章cert")
                    Assert.AssertAsyn(testResult,signData.signature.length>0,"分散签章signature")
                    Assert.AssertAsyn(testResult,signData.seal.length>0,"分散签章seal")
                    seq.resolve(signData)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,signData){
            sealContext[name].verifySignSeal({
                data: "123456",
                cert: signData.cert,
                signature:signData.signature,
                seal:signData.seal,
                timeStmap:signData.timeStamp,
                signTime:signData.signTime,
                show: {
                    divId: "seal_show",
                    ui: HebcaMiddleware.uiBootstrap,
                },
                success:function (){
                    seq.resolve(signData)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,signData){
            sealContext[name].getSealInfo({
                seal: signData.seal,
                success:function (info){
                    if(!notCheckInfo) {
                        Assert.AssertEqualAsyn(testResult, seal.sealSn, info.sealSn, "获取印章序列号")
                        Assert.AssertEqualAsyn(testResult, seal.sealName, info.sealName, "获取印章名称")
                    }
                    resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).onerror(function (e){
            reject(e)
        }).run()
    })
}

function test_signSealBatch(testResult,name,notCheckInfo){
    forEachSeal(testResult,name,function (seal,resolve,reject){
        var seq=new HebcaMiddleware.Seq();
        var times=5
        var data=[]
        for(var i=0;i<times;i++){
            data.push("123456--"+i)
        }
        var ss;
        seq.then(function (){
            sealContext[name].signSealBatch({
                sealSn: seal.sealSn,
                data: data,
                success:function (signData){
                    Assert.AssertAsyn(testResult,signData.length===times,"分散签章")
                    ss=signData
                    seq.resolve(0)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).thenEach(data,function (seq,index){
            sealContext[name].verifySignSeal({
                data: data[index],
                cert: ss[index].cert,
                signature:ss[index].signature,
                seal:ss[index].seal,
                timeStmap:ss[index].timeStamp,
                signTime:ss[index].signTime,
                show: {
                    divId: "seal_show",
                    ui: HebcaMiddleware.uiBootstrap,
                },
                success:function (){
                    seq.resolve(index+1)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (){
            resolve()
        }).onerror(function (e){
            reject(e)
        }).run()
    })
}

function test_verifyCert(testResult,name,certVerify){
    forEachSeal(testResult,name,function (seal,resolve,reject){
        var seq=new HebcaMiddleware.Seq();
        var curCert=null
        seq.then(function (){
            m.getCertInfo({
                sn: seal.certSn,
                success: function (cert){
                    curCert=cert.cert
                    seq.resolve(cert.cert)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (seq,cert){
            sealContext[name].verifyCert({
                cert:cert,
                certVerify:certVerify,
                success:function (){
                    resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).onerror(function (e){
            reject(e)
        }).run()
    })
}

function test_verifySeal(testResult,name,certVerify,sealVerify){
    forEachSeal(testResult,name,function (seal,resolve,reject){
        var seq=new HebcaMiddleware.Seq();
        var curSeal=null;
        var curCert=null;
        seq.then(function (){
            m.getCertInfo({
                sn: seal.certSn,
                success: function (cert){
                    curCert=cert.cert
                    seq.resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (){
            sealContext[name].getSealB64({
                sealSn: seal.sealSn,
                success: function (seal){
                    curSeal=seal
                    seq.resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (){
            sealContext[name].verifySeal({
                seal:curSeal,
                cert:curCert,
                certVerify:certVerify,
                sealVerify:sealVerify,
                success:function (){
                    resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).onerror(function (e){
            reject(e)
        }).run()

    })
}

function test_SignSealHash(testResult,name,certVerify,sealVerify){
    forEachSeal(testResult,name,function (seal,resolve,reject){
        var seq=new HebcaMiddleware.Seq();
        var pdfFileSrc="1.pdf"
        var curSeal=null;
        var pdfPage="1";
        var pdfPos="200,200";
        var pdfFileDest="2.pdf"
        seq.then(function (){
            sealContext[name].getSealB64({
                sealSn: seal.sealSn,
                success: function (seal){
                    curSeal=seal
                    seq.resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function(){
            $.ajax({
                type: "POST",
                url: demoServer+'/demo/pdfHash',
                data: "pdfFile="+pdfFileSrc+"&seal="+curSeal+"&page="+pdfPage+"&pos="+pdfPos,
                success: function (resp) {
                    if(resp.ret===0){
                        seq.resolve(resp.hash)
                    }else{
                        seq.reject(new Error("服务端返回错误:"+resp.message))
                    }
                },
                error: function (e) {
                    seq.reject(e)
                }
            })
        }).then(function (seq,hash){
            sealContext[name].signSealHash({
                sealSn: seal.sealSn,
                hash: hash,
                certVerify:certVerify,
                sealVerify:sealVerify,
                success:function (sealSignature) {
                    seq.resolve(sealSignature)
                },
                fail:function (e) {
                    seq.reject(e)
                }
            })
        }).then(function (seq,sealSignature){
            $.ajax({
                type: "POST",
                url: demoServer+'/demo/pdfMerge',
                data: "pdfFile="+pdfFileDest +"&sealSignature="+sealSignature,
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
            $.ajax({
                type: "POST",
                url: demoServer+'/demo/verifyPdf',
                data: "pdfFile="+pdfFileDest,
                success: function (resp) {
                    if(resp.ret===0){
                        resolve()
                    }else{
                        seq.reject(new Error(resp.message))
                    }
                },
                error: function (e) {
                    seq.reject(e)
                }
            })
        })
            .onerror(function (e){
            reject(e)
        }).run()
    })

}