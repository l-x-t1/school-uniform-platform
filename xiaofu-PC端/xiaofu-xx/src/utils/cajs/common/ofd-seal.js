
function ofdSignClient(){
    var code=`new HebcaMiddleware.FileSeal({    /*创建文件签章对象*/
    context: 'default-stand-alone',     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
    success:function (s) {
        /*ofd客户端签章*/
        s.signOfd({
            inFile: fileRoot+"files/1.ofd",  /*输入文件路径*/
            outFile: fileRoot+"files/2.ofd",  /*输出文件路径*/
            command: {          /*盖章命令*/
                sealType: 0,   /*签章类型，0：签章，1：签批 */
                location: {    /*定位方式*/
                    type: 0,   /* 0：固定坐标，1：关键字， 2：骑缝， 3：双开骑缝 */
                    pos: {     /* 坐标 */
                        page: "1",
                        coordinate: "100,100"
                    }
                }
            },
            enableTimeStamp: false,  /*关闭时间戳*/
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
    showCode('code',code)
}


function ofdSignClientSeq(){
    var code=`var seq=new HebcaMiddleware.Seq();
seq.then(function (){
    new HebcaMiddleware.FileSeal({    /*创建文件签章对象*/
        context: 'default-stand-alone',     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
        success: function (s) {
            seq.resolve(s)
        },
        fail: function (e) {
            seq.reject(e.message)
        }
    })
}).then(function(seq,s){
    /*ofd客户端签章*/
    s.signOfd({
        inFile: fileRoot+"files/1.ofd",  /*输入文件路径*/
        outFile: fileRoot+"files/2.ofd",  /*输出文件路径*/
        command: {          /*盖章命令*/
            sealType: 0,   /*签章类型，0：签章，1：签批 */
            location: {    /*定位方式*/
                type: 0,   /* 0：固定坐标，1：关键字， 2：骑缝， 3：双开骑缝 */
                pos: {     /* 坐标 */
                    page: "1",
                    coordinate: "100,100"
                }
            }
        },
        enableTimeStamp: false,  /*关闭时间戳*/
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

function ofdSignClientPromise(){
    var code=`HebcaMiddlewareX.createFileSeal({    /*创建文件签章对象*/
        context: 'default-stand-alone'     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
}).then(s=>{
    /*ofd客户端签章*/
    return s.signOfd({
        inFile: fileRoot+"files/1.ofd",  /*输入文件路径*/
        outFile: fileRoot+"files/2.ofd",  /*输出文件路径*/
        command: {          /*盖章命令*/
            sealType: 0,   /*签章类型，0：签章，1：签批 */
            location: {    /*定位方式*/
                type: 0,   /* 0：固定坐标，1：关键字， 2：骑缝， 3：双开骑缝 */
                pos: {     /* 坐标 */
                    page: "1",
                    coordinate: "100,100"
                }
            }
        },
        enableTimeStamp: false  /*不启用时间戳*/
    })
}).then(()=>{
    alert("盖章成功")
}).catch(e=>{
    alert(e.message)
})`
    showCode('code_promise',code)
}

function ofdSignClientAsync(){
    var code=`(async function(){
    try
    {
        let fs=await HebcaMiddlewareX.createFileSeal({    /*创建文件签章对象*/
            context: 'default-stand-alone'     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
        })
        
        /*ofd客户端签章*/
        await fs.signOfd({
            inFile: fileRoot+"files/1.ofd",  /*输入文件路径*/
            outFile: fileRoot+"files/2.ofd",  /*输出文件路径*/
            command: {          /*盖章命令*/
                sealType: 0,   /*签章类型，0：签章，1：签批 */
                location: {    /*定位方式*/
                    type: 0,   /* 0：固定坐标，1：关键字， 2：骑缝， 3：双开骑缝 */
                    pos: {     /* 坐标 */
                        page: "1",
                        coordinate: "100,100"
                    }
                }
            },
            enableTimeStamp: false  /*不启用时间戳*/
        })

        alert("盖章成功")
    }
    catch (e){
        alert(e.message)
    }
})()`
    showCode('code_async',code)

}

function ofdSignHash(){
    var code=`new HebcaMiddleware.Seal({          /*创建签章对象*/
    context: 'default-stand-alone',     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
    success:function (s) {
        /*联合签章，选择印章*/
        s.selectSealHash({
            success:function (sealInfo){
                /*发送应用服务器，由应用服务器调用密码云，进行ofd摘要，应用服务器根据情况定义自己的协议*/
                $.ajax({
                    type: "POST",
                    url: demoServer + '/demo/ofdHash',
                    data: "ofdFile=" + ofdServerFileSrc + "&seal=" + sealInfo.seal + "&cert=" + sealInfo.cert + "&sealType=0&type=0&page=1&pos=100,100",  /*这里以固定坐标为例*/
                    success: function (resp) {
                        if (resp.ret === 0) {
                            /*创建中间件对象*/
                            new HebcaMiddleware.Middleware({
                                success:function (m){
                                    /*客户端签名*/
                                    m.sign({
                                        sn: sealInfo.certSn,
                                        data: resp.hash,
                                        dataFormat: "Base64",
                                        success: function (signature) {
                                            /*应用服务器调用密码云完成合并*/
                                            $.ajax({
                                                type: "POST",
                                                url: demoServer + '/demo/ofdMerge',
                                                data: "ofdFile=" + ofdServerFileDest + "&signature=" +  signature.signature + "&hash=" + resp.hash,
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
                                },
                                fail:function (e){
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


function ofdSignHashSeq(){
    var code=`var seq=new HebcaMiddleware.Seq();
var s
var sealInfo
var hash
seq.then(function (){
    new HebcaMiddleware.Seal({          /*创建签章对象*/
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
    /*发送应用服务器，由应用服务器调用密码云，进行ofd摘要，应用服务器根据情况定义自己的协议*/
    $.ajax({
        type: "POST",
        url: demoServer + '/demo/ofdHash',
        data: "ofdFile=" + ofdServerFileSrc + "&seal=" + sealInfo.seal + "&cert=" + sealInfo.cert + "&sealType=0&type=0&page=1&pos=100,100",  /*这里以固定坐标为例*/
        success: function (resp) {
            if (resp.ret === 0) {
                hash=resp.hash
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
    /*创建中间件对象*/
    new HebcaMiddleware.Middleware({
        success:function (m) {
            seq.resolve(m)
        },
        fail:function (e){
            seq.reject(e)
        }
    })
}).then(function (seq,m){
    /*客户端签名*/
    m.sign({
        sn: sealInfo.certSn,
        data: hash,
        dataFormat: "Base64",
        success: function (signature) {
            seq.resolve(signature)
        },
        fail: function (e) {
            seq.reject(e)
        }
    })
}).then(function (seq,signature){
    /*应用服务器调用密码云完成合并*/
    $.ajax({
        type: "POST",
        url: demoServer + '/demo/ofdMerge',
        data: "ofdFile=" + ofdServerFileDest + "&signature=" +  signature.signature + "&hash=" + hash,
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

function ofdSignHashPromise(){

    var code=`var s
var sealInfo
var hash
HebcaMiddlewareX.createSeal({          /*创建签章对象*/
    context: 'default-stand-alone',     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
}).then(sealObj=>{
    s=sealObj
    /*联合签章，选择印章*/
    return s.selectSealHash()
}).then(info=>{
    sealInfo=info
    return new Promise((resolve, reject) => {
        /*发送应用服务器，由应用服务器调用密码云，进行ofd摘要，应用服务器根据情况定义自己的协议*/
        $.ajax({
            type: "POST",
            url: demoServer + '/demo/ofdHash',
            data: "ofdFile=" + ofdServerFileSrc + "&seal=" + sealInfo.seal + "&cert=" + sealInfo.cert + "&sealType=0&type=0&page=1&pos=100,100",  /*这里以固定坐标为例*/
            success: function (resp) {
                if (resp.ret === 0) {
                    hash=resp.hash
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
    /*创建中间件对象*/
    return HebcaMiddlewareX.createMiddleware()
}).then(m=>{
    /*客户端签名*/
    return m.sign({
        sn: sealInfo.certSn,
        data: hash,
        dataFormat: "Base64",
    })
}).then(signature=>{
    return new Promise((resolve, reject) => {
        /*应用服务器调用密码云完成合并*/
        $.ajax({
            type: "POST",
            url: demoServer + '/demo/ofdMerge',
            data: "ofdFile=" + ofdServerFileDest + "&signature=" +  signature.signature + "&hash=" + hash,
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

function ofdSignHashAsync(){

    var code=`(async ()=>{
    try{
        let s=await HebcaMiddlewareX.createSeal({          /*创建签章对象*/
            context: 'default-stand-alone',     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
        })
        /*联合签章，选择印章*/
        let sealInfo=await s.selectSealHash()

        /*发送应用服务器，由应用服务器调用密码云，进行ofd摘要，应用服务器根据情况定义自己的协议*/
        let hash=await new Promise((resolve, reject) => {
            $.ajax({
                type: "POST",
                url: demoServer + '/demo/ofdHash',
                data: "ofdFile=" + ofdServerFileSrc + "&seal=" + sealInfo.seal + "&cert=" + sealInfo.cert + "&sealType=0&type=0&page=1&pos=100,100",  /*这里以固定坐标为例*/
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

        /*创建中间件对象*/
        let m=await HebcaMiddlewareX.createMiddleware()

        /*客户端签名*/
        let signature=await m.sign({
            sn: sealInfo.certSn,
            data: hash,
            dataFormat: "Base64",
        })

        await new Promise((resolve, reject) => {
            /*应用服务器调用密码云完成合并*/
            $.ajax({
                type: "POST",
                url: demoServer + '/demo/ofdMerge',
                data: "ofdFile=" + ofdServerFileDest + "&signature=" +  signature.signature + "&hash=" + hash,
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
    }catch (e){
        alert(e.message)
    }
})()`
    showCode('code_async',code)

}


function ofdSignHashFx(){

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
                    /*客户端计算ofd摘要*/
                    fs.hashOfd({
                        inFile: fileRoot+"files/1.ofd",
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
                            $.ajax({
                                type: "POST",
                                url: demoServer+'/demo/certSign',
                                data: "certSubjectId="+certIdInCloud+"&dataB64="+data.data+"&password="+certPasswordInCloud,
                                success: function (resp) {
                                    if(resp.ret===0){
                                        /*客户端合并签名*/
                                        fs.mergeOfd({
                                            outFile: fileRoot+"files/2.ofd",
                                            signature: resp.signature,
                                            dataHash: data.dataHash,
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


function ofdSignHashFxSeq(){
    var code=`var seq=new HebcaMiddleware.Seq()
var fs
var certB64
var sealB64
var data
seq.then(function (){
    new HebcaMiddleware.FileSeal({          /*创建文件签章对象*/
        context: 'default-stand-alone',     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
        success:function (fileSealObj) {
            fs=fileSealObj
            seq.resolve()
        },
        fail:function (e){
            seq.reject(e)
        }
    })
}).then(function (){
    /*从服务端获取印章*/
    $.ajax({
        type: "POST",
        url: demoServer+'/demo/getSeal',
        data: "sealSn="+sealSnInCloud,  /*密码云中的印章序列号，要盖章的印章*/
        success: function (resp) {
            if(resp.ret===0){
                certB64=resp.certB64
                sealB64=resp.sealB64
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
    /*客户端计算ofd摘要*/
    fs.hashOfd({
        inFile: fileRoot+"files/1.ofd",
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
        success: function (d) {
            data=d
            seq.resolve()
        },
        fail: function (e) {
            seq.reject(e)
        }
    })
}).then(function (){
    /*服务端签名，调用密码云使用P1签名*/
    $.ajax({
        type: "POST",
        url: demoServer+'/demo/certSign',
        data: "certSubjectId="+certIdInCloud+"&dataB64="+data.data+"&password="+certPasswordInCloud,
        success: function (resp) {
            if(resp.ret===0){
                seq.resolve(resp.signature)
            }else{
                seq.reject(new Error(resp.message))
            }
        },
        error: function (e) {
            seq.resolve(e)
        }
    })
}).then(function (seq,signature){
    /*客户端合并签名*/
    fs.mergeOfd({
        outFile: fileRoot+"files/2.ofd",
        signature: signature,
        dataHash: data.dataHash,
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

function ofdSignHashFxPromise(){
    var code=`var fs
var certB64
var sealB64
var data
HebcaMiddlewareX.createFileSeal({          /*创建文件签章对象*/
    context: 'default-stand-alone',     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
}).then(fileSealObj=>{
    fs=fileSealObj
    return new Promise((resolve, reject) => {
        /*从服务端获取印章*/
        $.ajax({
            type: "POST",
            url: demoServer+'/demo/getSeal',
            data: "sealSn="+sealSnInCloud,  /*密码云中的印章序列号，要盖章的印章*/
            success: function (resp) {
                if(resp.ret===0){
                    certB64=resp.certB64
                    sealB64=resp.sealB64
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
    /*客户端计算ofd摘要*/
    return fs.hashOfd({
        inFile: fileRoot+"files/1.ofd",
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
    })
}).then(d=>{
    data=d
    return new Promise((resolve, reject) => {
        /*服务端签名，调用密码云使用P1签名*/
        $.ajax({
            type: "POST",
            url: demoServer+'/demo/certSign',
            data: "certSubjectId="+certIdInCloud+"&dataB64="+data.data+"&password="+certPasswordInCloud,
            success: function (resp) {
                if(resp.ret===0){
                    resolve(resp.signature)
                }else{
                    reject(new Error(resp.message))
                }
            },
            error: function (e) {
                resolve(e)
            }
        })
    })
}).then(signature=>{
    /*客户端合并签名*/
    return fs.mergeOfd({
        outFile: fileRoot+"files/2.ofd",
        signature: signature,
        dataHash: data.dataHash,
        enableTimeStamp:false,
    })
}).then(()=>{
    alert('盖章成功')
}).catch(e=>{
    alert(e.message)
})`
    showCode('code_promise',code)

}


function ofdSignHashFxAsync(){

    var code=`(async ()=>{
    try{
        let fs=await HebcaMiddlewareX.createFileSeal({          /*创建文件签章对象*/
            context: 'default-stand-alone',     /*设置签章环境，这里以单机版举例，可以设置其它环境*/
        })

        let resp=await new Promise((resolve, reject) => {
            /*从服务端获取印章*/
            $.ajax({
                type: "POST",
                url: demoServer+'/demo/getSeal',
                data: "sealSn="+sealSnInCloud,  /*密码云中的印章序列号，要盖章的印章*/
                success: function (resp) {
                    if(resp.ret===0){
                        resolve(resp)
                    }else{
                        reject(new Error(resp.message))
                    }
                },
                error: function (e) {
                    reject(e)
                }
            })
        })

        /*客户端计算ofd摘要*/
        let data=await fs.hashOfd({
            inFile: fileRoot+"files/1.ofd",
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
        })

        let signature=await new Promise((resolve, reject) => {
            /*服务端签名，调用密码云使用P1签名*/
            $.ajax({
                type: "POST",
                url: demoServer+'/demo/certSign',
                data: "certSubjectId="+certIdInCloud+"&dataB64="+data.data+"&password="+certPasswordInCloud,
                success: function (resp) {
                    if(resp.ret===0){
                        resolve(resp.signature)
                    }else{
                        reject(new Error(resp.message))
                    }
                },
                error: function (e) {
                    resolve(e)
                }
            })
        })

        /*客户端合并签名*/
        await fs.mergeOfd({
            outFile: fileRoot+"files/2.ofd",
            signature: signature,
            dataHash: data.dataHash,
            enableTimeStamp:false,
        })

        alert('盖章成功')
    }
    catch (e){
        alert(e.message)
    }

})()`
    showCode('code_async',code)

}