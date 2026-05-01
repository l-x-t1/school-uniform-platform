
function pdfSignClient(){
    var code=`new HebcaMiddleware.FileSeal({    /*创建文件签章对象*/
        context: 'default-stand-alone',     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
        success:function (s) {
            /*pdf客户端签章*/
            s.signPdf({
                inFile: fileRoot+"files/1.pdf",  /*输入文件路径*/
                outFile: fileRoot+"files/2.pdf",  /*输出文件路径*/
                command: {          /*盖章命令*/
                    sealType: 0,   /*签章类型，0：签章，1：文字签批，2：图片签批 */
                    location: {    /*定位方式*/
                        type: 0,   /* 0：固定坐标，1：关键字， 2：签名域， 3：骑缝和双开骑缝 */
                        pos: {     /* 坐标 */
                            page: "1",
                            coordinate: "100,100"
                        }
                    }
                },
                enableTimeStamp: true,  /*启用时间戳*/
                success: function (){
                    alert("盖章成功")
                },
                fail:function (e){
                    alert(e.message)
                }
            })
        },
        fail:function (e) {
            alert(e.message)
        }
    })`
    showCode("code",code)
}

function pdfSignClientSeq(){
    var code=`var seq=new HebcaMiddleware.Seq()
    seq.then(function () {
        new HebcaMiddleware.FileSeal({    /*创建文件签章对象*/
            context: 'default-stand-alone',     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
            success: function (s) {
                seq.resolve(s)
            },
            fail: function (e) {
                seq.reject(e)
            }
        })
    }).then(function(seq,s){
        /*pdf客户端签章*/
        s.signPdf({
            inFile: fileRoot+"files/1.pdf",  /*输入文件路径*/
            outFile: fileRoot+"files/2.pdf",  /*输出文件路径*/
            command: {          /*盖章命令*/
                sealType: 0,   /*签章类型，0：签章，1：文字签批，2：图片签批 */
                location: {    /*定位方式*/
                    type: 0,   /* 0：固定坐标，1：关键字， 2：签名域， 3：骑缝和双开骑缝 */
                    pos: {     /* 坐标 */
                        page: "1",
                        coordinate: "100,100"
                    }
                }
            },
            enableTimeStamp: true,  /*启用时间戳*/
            success: function (){
                alert("盖章成功")
            },
            fail:function (e){
                seq.reject(e)
            }
        })
    }).onerror(function (e){
        alert(e.message)
    }).run()`
    showCode('code_seq',code)
}

function pdfSignClientPromise(){
    var code=`HebcaMiddlewareX.createFileSeal({    /*创建文件签章对象*/
        context: 'default-stand-alone'     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
}).then(s=>{
    /*pdf客户端签章*/
    return s.signPdf({
        inFile: fileRoot+"files/1.pdf",  /*输入文件路径*/
        outFile: fileRoot+"files/2.pdf",  /*输出文件路径*/
        command: {          /*盖章命令*/
            sealType: 0,   /*签章类型，0：签章，1：文字签批，2：图片签批 */
            location: {    /*定位方式*/
                type: 0,   /* 0：固定坐标，1：关键字， 2：签名域， 3：骑缝和双开骑缝 */
                pos: {     /* 坐标 */
                    page: "1",
                    coordinate: "100,100"
                }
            }
        },
        enableTimeStamp: true  /*启用时间戳*/
    })
}).then(()=>{
    alert("盖章成功")
}).catch(e=>{
    alert(e.message)
})`
    showCode('code_promise',code)
}

function pdfSignClientAsync(){
    var code=`(async function(){
    try
    {
        let fs=await HebcaMiddlewareX.createFileSeal({    /*创建文件签章对象*/
            context: 'default-stand-alone'     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
        })
        
        /*pdf客户端签章*/
        await fs.signPdf({
            inFile: fileRoot+"files/1.pdf",  /*输入文件路径*/
            outFile: fileRoot+"files/2.pdf",  /*输出文件路径*/
            command: {          /*盖章命令*/
                sealType: 0,   /*签章类型，0：签章，1：文字签批，2：图片签批 */
                location: {    /*定位方式*/
                    type: 0,   /* 0：固定坐标，1：关键字， 2：签名域， 3：骑缝和双开骑缝 */
                    pos: {     /* 坐标 */
                        page: "1",
                        coordinate: "100,100"
                    }
                }
            },
            enableTimeStamp: true  /*启用时间戳*/
        })

        alert("盖章成功")
    }
    catch (e){
        alert(e.message)
    }
})()`
    showCode('code_async',code)

}

function pdfSignHash(){
    var code=`new HebcaMiddleware.Seal({          /*创建签章对象*/
        context: 'default-stand-alone',     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
        success:function (s) {
            /*联合签章，选择印章*/
            s.selectSealHash({
                success:function (sealInfo){
                    /*发送应用服务器，由应用服务器调用密码云，进行pdf摘要，应用服务器根据情况定义自己的协议*/
                    $.ajax({
                        type: "POST",
                        url: demoServer + '/demo/pdfHash',
                        data: "pdfFile=" + pdfServerFileSrc + "&seal=" + sealInfo.seal + "&cert=" + sealInfo.cert + "&sealType=0&type=0&page=1&pos=100,100",  /*这里以固定坐标为例*/
                        success: function (resp) {
                            if (resp.ret === 0) {
                                /*客户端签署P7*/
                                s.signSealHash({
                                    sealSn: sealInfo.sealSn,
                                    hash: resp.hash,
                                    success: function (sealSignature) {
                                        /*应用服务器调用密码云完成合并*/
                                        $.ajax({
                                            type: "POST",
                                            url: demoServer + '/demo/pdfMerge',
                                            data: "pdfFile=" + pdfServerFileDest + "&sealSignature=" + sealSignature,
                                            success: function (resp) {
                                                if (resp.ret === 0) {
                                                    alert("盖章成功")
                                                } else {
                                                    alert("服务端返回错误:" + resp.message)
                                                }
                                            },
                                            error: function (e) {
                                                alert(e.message)
                                            }
                                        })
                                    },
                                    fail: function (e) {
                                        alert(e.message)
                                    }
                                })

                            } else {
                                alert("服务端返回错误:" + resp.message)
                            }
                        },
                        error: function (e) {
                            alert(e.message)
                        }
                    })
                },
                fail:function (e){
                    alert(e.message)
                }
            })
        },
        fail:function (e) {
            alert(e.message)
        }
    })`
    showCode('code',code)
}


function pdfSignHashSeq(){
    var code=`var seq=new HebcaMiddleware.Seq()
    var s;
    var sealInfo;
    var hash;
    var sealSignature;
    seq.then(function (){
        /*创建签章对象*/
        new HebcaMiddleware.Seal({
            context: 'default-stand-alone',     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
            success: function (sealObj) {
                s=sealObj
                seq.resolve()
            },
            fail: function (e) {
                seq.reject(e)
            }
        })
    }).then(function(){
        /*联合签章，选择印章*/
        s.selectSealHash({
            success: function (info) {
                sealInfo=info
                seq.resolve()
            },
            fail: function (e) {
                seq.reject(e)
            }
        })
    }).then(function(){
        /*发送应用服务器，由应用服务器调用密码云，进行pdf摘要，应用服务器根据情况定义自己的协议*/
        $.ajax({
            type: "POST",
            url: demoServer + '/demo/pdfHash',
            data: "pdfFile=" + pdfServerFileSrc + "&seal=" + sealInfo.seal + "&cert=" + sealInfo.cert + "&sealType=0&type=0&page=1&pos=100,100",  /*这里以固定坐标为例*/
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
    }).then(function (){
        /*客户端签署P7*/
        s.signSealHash({
            sealSn: sealInfo.sealSn,
            hash: hash,
            success: function (signature) {
                sealSignature=signature
                seq.resolve()
            },
            fail: function (e) {
                seq.reject(e)
            }
        })
    }).then(function (){
        /*应用服务器调用密码云完成合并*/
        $.ajax({
            type: "POST",
            url: demoServer + '/demo/pdfMerge',
            data: "pdfFile=" + pdfServerFileDest + "&sealSignature=" + sealSignature,
            success: function (resp) {
                if (resp.ret === 0) {
                    alert("盖章成功")
                } else {
                    seq.reject(new Error("服务端返回错误:" + resp.message))
                }
            },
            error: function (e) {
                seq.reject(e)
            }
        })
    }).onerror(function (e){
        alert(e.message)
    }).run()`
    showCode('code_seq',code)

}


function pdfSignHashPromise(){

    var code=`var s;
var sealInfo;
var sealSignature;
/*创建签章对象*/
HebcaMiddlewareX.createSeal({
    context: 'default-stand-alone',     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
}).then(sealObj=>{
    s=sealObj
    /*联合签章，选择印章*/
    return s.selectSealHash()
}).then(info=>{
    sealInfo=info
    return new Promise(function (resolve, reject){
        /*发送应用服务器，由应用服务器调用密码云，进行pdf摘要，应用服务器根据情况定义自己的协议*/
        $.ajax({
            type: "POST",
            url: demoServer + '/demo/pdfHash',
            data: "pdfFile=" + pdfServerFileSrc + "&seal=" + sealInfo.seal + "&cert=" + sealInfo.cert + "&sealType=0&type=0&page=1&pos=100,100",  /*这里以固定坐标为例*/
            success: function (resp) {
                if (resp.ret === 0) {
                    resolve(resp.hash)
                } else {
                    reject(new Error("服务端返回错误:" + resp.message))
                }
            },
            error: function (e) {
                reject(e)
            }
        })
    })
}).then(hash=>{
    /*客户端签署P7*/
    return s.signSealHash({
        sealSn: sealInfo.sealSn,
        hash: hash,
    })
}).then(sealSignature=>{
    return new Promise(function (resolve, reject){
        /*应用服务器调用密码云完成合并*/
        $.ajax({
            type: "POST",
            url: demoServer + '/demo/pdfMerge',
            data: "pdfFile=" + pdfServerFileDest + "&sealSignature=" + sealSignature,
            success: function (resp) {
                if (resp.ret === 0) {
                    resolve()
                } else {
                    reject(new Error("服务端返回错误:" + resp.message))
                }
            },
            error: function (e) {
                reject(e)
            }
        })
    })
}).then(()=>{
    alert("盖章成功")
}).catch(e=>{
    alert(e.message)
})`
    showCode('code_promise',code)

}


function pdfSignHashAsync(){

    var code=`(async function (){
    try
    {
        /*创建签章对象*/
        let s=await HebcaMiddlewareX.createSeal({
            context: 'default-stand-alone',     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
        })
        /*联合签章，选择印章*/
        let sealInfo=await s.selectSealHash()
        /*发送应用服务器，由应用服务器调用密码云，进行pdf摘要，应用服务器根据情况定义自己的协议*/
        let hash=await new Promise(function (resolve, reject){
            $.ajax({
                type: "POST",
                url: demoServer + '/demo/pdfHash',
                data: "pdfFile=" + pdfServerFileSrc + "&seal=" + sealInfo.seal + "&cert=" + sealInfo.cert + "&sealType=0&type=0&page=1&pos=100,100",  /*这里以固定坐标为例*/
                success: function (resp) {
                    if (resp.ret === 0) {
                        resolve(resp.hash)
                    } else {
                        reject(new Error("服务端返回错误:" + resp.message))
                    }
                },
                error: function (e) {
                    reject(e)
                }
            })
        })

        /*客户端签署P7*/
        let sealSignature=await s.signSealHash({
            sealSn: sealInfo.sealSn,
            hash: hash,
        })

        /*应用服务器调用密码云完成合并*/
        await new Promise(function (resolve, reject){
            $.ajax({
                type: "POST",
                url: demoServer + '/demo/pdfMerge',
                data: "pdfFile=" + pdfServerFileDest + "&sealSignature=" + sealSignature,
                success: function (resp) {
                    if (resp.ret === 0) {
                        resolve()
                    } else {
                        reject(new Error("服务端返回错误:" + resp.message))
                    }
                },
                error: function (e) {
                    reject(e)
                }
            })
        })

        alert("盖章成功")
    }
    catch (e){
        alert(e.message)
    }
    })()`
    showCode('code_async',code)
}

function pdfSignHashFx(){

    var code=`new HebcaMiddleware.FileSeal({          /*创建文件签章对象*/
        context: 'default-stand-alone',     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
        success:function (fs) {
            /*从服务端获取印章*/
            $.ajax({
                type: "POST",
                url: demoServer+'/demo/getSeal',
                data: "sealSn="+sealSnInCloud,  /*密码云中的印章序列号，要盖章的印章*/
                success: function (resp) {
                    if(resp.ret===0){
                        /*客户端计算PDF摘要*/
                        fs.hashPdf({
                            inFile: fileRoot+"files/1.pdf",
                            command: {   /*盖章命令*/
                                sealType: 0,    
                                seal: resp.sealB64,    /*印章，来自密码云*/
                                signCert: resp.certB64,  /*证书，来自密码云*/
                                location: {   /*以固定坐标举例*/
                                    type: 0,
                                    pos: {
                                        page: "1",
                                        coordinate: "100,100"
                                    }
                                }
                            },
                            enableTimeStamp: false,  
                            success: function (data) {
                                /*服务端签名，调用密码云使用P1签名*/
                                var hashParts=data.split(";")
                                $.ajax({
                                    type: "POST",
                                    url: demoServer+'/demo/certSign',
                                    data: "certSubjectId="+certIdInCloud+"&dataB64="+hashParts[1]+"&password="+certPasswordInCloud,
                                    success: function (resp) {
                                        if(resp.ret===0){
                                            /*客户端合并签名*/
                                            fs.mergePdf({
                                                outFile: fileRoot+"files/2.pdf",
                                                signature: hashParts[0]+";"+resp.signature,
                                                enableTimeStamp:false,
                                                success: function () {
                                                    alert("盖章成功")
                                                },
                                                fail: function (e) {
                                                    alert(e.message)
                                                }
                                            })
                                        }else{
                                            alert(resp.message)
                                        }
                                    },
                                    error: function (e) {
                                        alert(e.message)
                                    }
                                })
                            },
                            fail: function (e) {
                                alert(e.message)
                            }
                        })
                    }else{
                        alert(resp.message)
                    }
                },
                error: function (e) {
                    alert(e.message)
                }
            })
        },
        fail:function (e){
            alert(e.message)
        }
    })`
    showCode('code',code)

}


function pdfSignHashFx(){

    var code=`new HebcaMiddleware.FileSeal({          /*创建文件签章对象*/
        context: 'default-stand-alone',     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
        success:function (fs) {
            /*从服务端获取印章*/
            $.ajax({
                type: "POST",
                url: demoServer+'/demo/getSeal',
                data: "sealSn="+sealSnInCloud,  /*密码云中的印章序列号，要盖章的印章*/
                success: function (resp) {
                    if(resp.ret===0){
                        /*客户端计算PDF摘要*/
                        fs.hashPdf({
                            inFile: fileRoot+"files/1.pdf",
                            command: {   /*盖章命令*/
                                sealType: 0,    
                                seal: resp.sealB64,    /*印章，来自密码云*/
                                signCert: resp.certB64,  /*证书，来自密码云*/
                                location: {   /*以固定坐标举例*/
                                    type: 0,
                                    pos: {
                                        page: "1",
                                        coordinate: "100,100"
                                    }
                                }
                            },
                            enableTimeStamp: false,  
                            success: function (data) {
                                /*服务端签名，调用密码云使用P1签名*/
                                var hashParts=data.split(";")
                                $.ajax({
                                    type: "POST",
                                    url: demoServer+'/demo/certSign',
                                    data: "certSubjectId="+certIdInCloud+"&dataB64="+hashParts[1]+"&password="+certPasswordInCloud,
                                    success: function (resp) {
                                        if(resp.ret===0){
                                            /*客户端合并签名*/
                                            fs.mergePdf({
                                                outFile: fileRoot+"files/2.pdf",
                                                signature: hashParts[0]+";"+resp.signature,
                                                enableTimeStamp:false,
                                                success: function () {
                                                    alert("盖章成功")
                                                },
                                                fail: function (e) {
                                                    alert(e.message)
                                                }
                                            })
                                        }else{
                                            alert(resp.message)
                                        }
                                    },
                                    error: function (e) {
                                        alert(e.message)
                                    }
                                })
                            },
                            fail: function (e) {
                                alert(e.message)
                            }
                        })
                    }else{
                        alert(resp.message)
                    }
                },
                error: function (e) {
                    alert(e.message)
                }
            })
        },
        fail:function (e){
            alert(e.message)
        }
    })`
    showCode('code',code)

}

function pdfSignHashFxSeq(){

    var code=`var seq=new HebcaMiddleware.Seq()
var fs
var sealB64
var certB64
var data
var signature
var hashParts
seq.then(function (){
    new HebcaMiddleware.FileSeal({          /*创建文件签章对象*/
        context: 'default-stand-alone',     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
        success: function (fsObj) {
            fs=fsObj
            seq.resolve()
        },
        fail: function (e) {
            seq.reject(e)
        }
    })
}).then(function(){
    /*从服务端获取印章*/
    $.ajax({
        type: "POST",
        url: demoServer + '/demo/getSeal',
        data: "sealSn=" + sealSnInCloud,  /*密码云中的印章序列号，要盖章的印章*/
        success: function (resp) {
            if (resp.ret === 0) {
                sealB64=resp.sealB64
                certB64=resp.certB64
                seq.resolve()
            } else {
                seq.reject(new Error(resp.message))
            }
        },
        error: function (e) {
            seq.reject(e)
        }
    })
}).then(function (){
    /*客户端计算PDF摘要*/
    fs.hashPdf({
        inFile: fileRoot+"files/1.pdf",
        command: {   /*盖章命令*/
            sealType: 0,
            seal: sealB64,    /*印章，来自密码云*/
            signCert: certB64,  /*证书，来自密码云*/
            location: {   /*以固定坐标举例*/
                type: 0,
                pos: {
                    page: "1",
                    coordinate: "100,100"
                }
            }
        },
        enableTimeStamp: false,
        success: function (hashData) {
            data=hashData
            seq.resolve()
        },
        fail: function (e) {
            seq.reject(e)
        }
    })
}).then(function(){
    /*服务端签名，调用密码云使用P1签名*/
    hashParts=data.split(";")
    $.ajax({
        type: "POST",
        url: demoServer+'/demo/certSign',
        data: "certSubjectId="+certIdInCloud+"&dataB64="+hashParts[1]+"&password="+certPasswordInCloud,
        success: function (resp) {
            if(resp.ret===0){
                signature=resp.signature
                seq.resolve()
            }else{
                seq.reject(new Error(resp.message))
            }
        },
        error: function (e) {
            seq.reject(e)
        }
    })
}).then(function (){
    /*客户端合并签名*/
    fs.mergePdf({
        outFile: fileRoot+"files/2.pdf",
        signature: hashParts[0]+";"+signature,
        enableTimeStamp:false,
        success: function () {
            alert("盖章成功")
        },
        fail: function (e) {
            seq.reject(e)
        }
    })
}).onerror(function (e){
    alert(e.message)
}).run()`
    showCode('code_seq',code)

}



function pdfSignHashFxPromise(){

    var code=`var fs
var sealB64
var certB64
var data
var signature
var hashParts
HebcaMiddlewareX.createFileSeal({          /*创建文件签章对象*/
    context: 'default-stand-alone',     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
}).then(fsObj=>{
    fs=fsObj
    /*从服务端获取印章*/
    return new Promise((resolve, reject) => {
        $.ajax({
            type: "POST",
            url: demoServer + '/demo/getSeal',
            data: "sealSn=" + sealSnInCloud,  /*密码云中的印章序列号，要盖章的印章*/
            success: function (resp) {
                if (resp.ret === 0) {
                    sealB64=resp.sealB64
                    certB64=resp.certB64
                    resolve()
                } else {
                    reject(new Error(resp.message))
                }
            },
            error: function (e) {
                reject(e)
            }
        })
    })
}).then(()=>{
    /*客户端计算PDF摘要*/
    return fs.hashPdf({
        inFile: fileRoot+"files/1.pdf",
        command: {   /*盖章命令*/
            sealType: 0,
            seal: sealB64,    /*印章，来自密码云*/
            signCert: certB64,  /*证书，来自密码云*/
            location: {   /*以固定坐标举例*/
                type: 0,
                pos: {
                    page: "1",
                    coordinate: "100,100"
                }
            }
        },
        enableTimeStamp: false
    })
}).then(hashData=>{
    data=hashData
    /*服务端签名，调用密码云使用P1签名*/
    hashParts=data.split(";")
    return new Promise((resolve, reject) => {
        $.ajax({
            type: "POST",
            url: demoServer+'/demo/certSign',
            data: "certSubjectId="+certIdInCloud+"&dataB64="+hashParts[1]+"&password="+certPasswordInCloud,
            success: function (resp) {
                if(resp.ret===0){
                    signature=resp.signature
                    resolve()
                }else{
                    reject(new Error(resp.message))
                }
            },
            error: function (e) {
                reject(e)
            }
        })
    })
}).then(()=>{
    /*客户端合并签名*/
    return fs.mergePdf({
        outFile: fileRoot+"files/2.pdf",
        signature: hashParts[0]+";"+signature,
        enableTimeStamp:false
    })
}).then(()=>{
    alert("盖章成功")
}).catch(e=>{
    alert(e.message)
})`
    showCode('code_promise',code)
    
}

function pdfSignHashFxAsync(){

    var code=`(async ()=>{
        try{
            let fs=await HebcaMiddlewareX.createFileSeal({          /*创建文件签章对象*/
                context: 'default-stand-alone',     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
            })

            /*从服务端获取印章*/
            let resp=await new Promise((resolve, reject) => {
                $.ajax({
                    type: "POST",
                    url: demoServer + '/demo/getSeal',
                    data: "sealSn=" + sealSnInCloud,  /*密码云中的印章序列号，要盖章的印章*/
                    success: function (resp) {
                        if (resp.ret === 0) {
                            resolve(resp)
                        } else {
                            reject(new Error(resp.message))
                        }
                    },
                    error: function (e) {
                        reject(e)
                    }
                })
            })

            /*客户端计算PDF摘要*/
            let data=await fs.hashPdf({
                inFile: fileRoot+"files/1.pdf",
                command: {   /*盖章命令*/
                    sealType: 0,
                    seal: resp.sealB64,    /*印章，来自密码云*/
                    signCert: resp.certB64,  /*证书，来自密码云*/
                    location: {   /*以固定坐标举例*/
                        type: 0,
                        pos: {
                            page: "1",
                            coordinate: "100,100"
                        }
                    }
                },
                enableTimeStamp: false
            })

            /*服务端签名，调用密码云使用P1签名*/
            let hashParts=data.split(";")
            let signature=await new Promise((resolve, reject) => {
                $.ajax({
                    type: "POST",
                    url: demoServer+'/demo/certSign',
                    data: "certSubjectId="+certIdInCloud+"&dataB64="+hashParts[1]+"&password="+certPasswordInCloud,
                    success: function (resp) {
                        if(resp.ret===0){
                            resolve(resp.signature)
                        }else{
                            reject(new Error(resp.message))
                        }
                    },
                    error: function (e) {
                        reject(e)
                    }
                })
            })

            /*客户端合并签名*/
            await fs.mergePdf({
                outFile: fileRoot+"files/2.pdf",
                signature: hashParts[0]+";"+signature,
                enableTimeStamp:false
            })

            alert('盖章成功')
        }
        catch (e){
            alert(e.message)
        }
    })()`
    showCode('code_async',code)

}