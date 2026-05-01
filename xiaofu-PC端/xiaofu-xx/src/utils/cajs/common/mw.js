function sign(){
    var code=`new HebcaMiddleware.Middleware({  /* 创建中间件对象 */
    success:function (m) {
        /*执行签名*/
        m.sign({
            data: "12345678",   /*签名原文*/
            success: function (ret){
                alert(ret.cert)   /*签名证书*/
                alert(ret.signature)   /*签名结果*/
            },
            fail:function (e){
                alert(e.message)
            }
        })
    },
    fail:function (e) {
        alert(e.message)
    }
});`
    showCode("code",code);
}

function signSeq(){
    var code=`var seq=new HebcaMiddleware.Seq()
seq.then(function (){
    /*创建中间件对象*/
    new HebcaMiddleware.Middleware({
        success:function (m){
            seq.resolve(m)   /*返回中间件对象*/
        },
        fail:function (e){
            seq.reject(e)
        }
    })
}).then(function (seq,m){   /*从这个接收第一步创建的中间件对象*/
    /*执行签名*/
    m.sign({
        data:"12345678",  /*签名原文*/
        success:function (ret){
            alert(ret.cert)    /*签名证书*/
            alert(ret.signature)  /*签名结果*/
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

function signPromise(){
    var code=`HebcaMiddlewareX.createMiddleware()   /*创建中间件对象*/
    .then(m=>{     /*得到创建的中间件对象*/
        /*执行签名*/
        return m.sign({   
            data:"12345678"
        })
    }).then(ret=>{   /*得到签名结果*/
       alert(ret.cert)  /*签名证书*/
       alert(ret.signature) /*签名结果*/
    }).catch(e=>{
       alert(e.message)
    })`
    showCode('code_promise',code)
}

async function signAsync(){
    var code=`(async function(){
    try{
        let m=await HebcaMiddlewareX.createMiddleware()  /*创建中间件，并返回*/
        let ret=await m.sign({    /*签名*/
            data: "12345678"
        })
        alert(ret.cert)    /*获取签名证书和结果*/
        alert(ret.signature)
    }catch (e){
        alert(e.message)
    }
})()`
    showCode('code_async',code)

}

function getSignCert(){
    var code=`new HebcaMiddleware.Middleware({
    success: function (m) {
        /*选择证书*/
        m.selectCert({
            success: function (cert) {
                /*获取选中证书的信息*/
                str="cn:"+cert.subject.cn
                str+=",sn:"+cert.sn;
                str+=",deviceName:"+cert.deviceName
                str+=",cert:"+cert.cert
                alert(str)
            },
            fail: function (e) {
                alert(e.message)
            }
        })
    },
    fail: function (e) {
        alert(e.message)
    }
})`
    showCode('code',code)

}

function getSignCertSeq(){
    var code=`var seq=new HebcaMiddleware.Seq()
seq.then(function (){
    /*创建中间件对象*/
    new HebcaMiddleware.Middleware({
        success:function (m){
            seq.resolve(m)
        },
        fail:function (e){
            seq.reject(e)
        }
    })
}).then(function (seq,m){
    /*选择证书*/
    m.selectCert({
        success: function (cert) {
            /*获取选中证书的信息*/
            str="cn:"+cert.subject.cn
            str+=",sn:"+cert.sn;
            str+=",deviceName:"+cert.deviceName
            str+=",cert:"+cert.cert
            alert(str)
        },
        fail: function (e) {
            alert(e.message)
        }
    })
}).onerror(function (e){
    alert(e.message)
}).run()`
    showCode('code_seq',code)
}

function getSignCertPromise(){
    var code=`HebcaMiddlewareX.createMiddleware()
    .then(m=>{
        /*选择证书*/
        return m.selectCert()
    }).then(cert=>{
        /*获取选中证书的信息*/
        str="cn:"+cert.subject.cn
        str+=",sn:"+cert.sn;
        str+=",deviceName:"+cert.deviceName
        str+=",cert:"+cert.cert
        alert(str)
    }).catch(e=>{
       alert(e.message)
    })`
    showCode('code_promise',code)
}

function getSignCertAsync(){
    var code=`(async function(){
    try{
        let m=await HebcaMiddlewareX.createMiddleware()
        let cert=await m.selectCert()  /*选择证书*/
        /*获取选中证书的信息*/
        str="cn:"+cert.subject.cn
        str+=",sn:"+cert.sn;
        str+=",deviceName:"+cert.deviceName
        str+=",cert:"+cert.cert
        alert(str)
    }catch (e){
        alert(e.message)
    }
})()`
    showCode('code_async',code)
}

function pkcs7Sign(){
    var code=`new HebcaMiddleware.Middleware({
    success:function (m) {
        /*pkcs7签名*/
        m.signMessage({
            data: "12345678",   /*原文*/
            success: function (p7){
                /*p7签名结果*/
                alert(p7)
            },
            fail:function (e){
                alert(e.message)
            }
        })
    },
    fail:function (e) {
        alert(e.message)
    }
});`
    showCode("code",code);
}

function pkcs7SignSeq(){
    var code=`var seq=new HebcaMiddleware.Seq()
seq.then(function (){
    /*创建中间件对象*/
    new HebcaMiddleware.Middleware({
        success:function (m){
            seq.resolve(m)
        },
        fail:function (e){
            seq.reject(e)
        }
    })
}).then(function (seq,m){
    /*p7签名*/
    m.signMessage({
        data:"12345678",
        success:function (p7){
            /*p7签名结果*/
            alert(p7)
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

function pkcs7SignPromise(){
    var code=`HebcaMiddlewareX.createMiddleware()
    .then(m=>{
        /*p7签名*/
        return m.signMessage({
            data:"12345678"
        })
    }).then(p7=>{
        /*获取签名结果*/
       alert(p7)
    }).catch(e=>{
       alert(e.message)
    })`
    showCode('code_promise',code)
}

async function pkcs7SignAsync(){
    var code=`(async function(){
    try{
        let m=await HebcaMiddlewareX.createMiddleware()
        /*p7签名，并返回结果*/
        let p7=await m.signMessage({
            data: "12345678"
        })
        alert(p7)
    }catch (e){
        alert(e.message)
    }
})()`
    showCode('code_async',code)

}

function pkcs7Envelop(){
    var code=`new HebcaMiddleware.Middleware({
    success:function (m) {
        /*选择加密证书*/
        m.selectCert({
            certType: "crypt",
            success: function(cert){
                /*p7加密*/
                m.envelopeMessage({
                    data: "12345678",
                    certs: [cert.cert],   /*设置解密人证书，可以多个*/
                    success: function (p7){
                        /*获取p7加密结果*/
                        alert(p7)
                        
                        /*p7解密*/
                        m.unenvelopeMessage({
                            message: p7,
                            success: function (data){
                                /*获取解密结果*/
                                alert(data)
                            },
                            fail:function (e){
                                alert(e.message)
                            }
                        })
                    },
                    fail:function (e){
                        alert(e.message)
                    }
                })
            },
            fail: function(e){
                alert(e.message)
            }
        })
    },
    fail:function (e) {
        alert(e.message)
    }
});`
    showCode("code",code);
}

function pkcs7EnvelopSeq(){
    var code=`var seq=new HebcaMiddleware.Seq()
var client=null
seq.then(function (){
    /*创建中间件对象*/
    new HebcaMiddleware.Middleware({
        success:function (m){
            seq.resolve(m)
        },
        fail:function (e){
            seq.reject(e)
        }
    })
}).then(function (seq,m){
    /*选择加密证书*/
    client=m
    m.selectCert({
        certType:"crypt",
        success:function (cert){
            seq.resolve(cert)
        },
        fail:function (e){
            seq.reject(e)
        }
    })
}).then(function(seq,cert){
    /*p7加密*/
    client.envelopeMessage({
        data: "12345678",
        certs: [cert.cert],  /*设置解密人证书，可以设置多个*/
        success: function (p7){
            alert(p7)
            seq.resolve(p7)
        },
        fail:function(e){
            seq.reject(e)
        }
    })
}).then(function(seq,p7){
    /*p7解密*/
    client.unenvelopeMessage({
        message: p7,
        success: function (data){
            alert(data)
        },
        fail:function (e){
            alert(e.message)
        }
    })
}).onerror(function (e){
    alert(e.message)
}).run()`
    showCode('code_seq',code)
}

function pkcs7EnvelopPromise(){
    var code=`let client=null
HebcaMiddlewareX.createMiddleware()
    .then(m=>{
        /*选择加密证书*/
        client=m
        return m.selectCert({
            certType: "crypt"
        })
    }).then(cert=>{
        /*p7加密*/
        return client.envelopeMessage({
            data:"12345678",
            certs:[cert.cert]   /*设置解密人证书，可以多个*/
        })
    }).then(p7=>{
       /*p7解密*/
       alert(p7)
       return client.unenvelopeMessage({
            message: p7
       })
    }).then(data=>{
        alert(data)
    }).catch(e=>{
       alert(e.message)
    })`
    showCode('code_promise',code)
}

async function pkcs7EnvelopAsync(){
    var code=`(async function(){
    try{
        let m=await HebcaMiddlewareX.createMiddleware()
        let cert=await m.selectCert({    /*选择加密证书*/
            certType: "crypt"
        })
        let p7=await m.envelopeMessage({   /*p7加密*/
            data: "12345678",
            certs:[cert.cert]   /*设置解密人证书，可以多个*/
        })
        alert(p7)
        let data=await m.unenvelopeMessage({  /*p7解密*/
            message: p7
        })
        alert(data)
    }catch (e){
        alert(e.message)
    }
})()`
    showCode('code_async',code)
}

function waitKeyEvent(){
    var code=`new HebcaMiddleware.Middleware({
    success:function (m) {
        /*事件回调函数定义*/
        function waitcallback(event,param){
            /*触发回调事件后，进行处理，这里演示获取当前所有证书信息*/
            m.getCerts({
                success:function (certs){
                    var str=event+": "+param.deviceName+", certs: "
                    for(var i=0;i<certs.length;i++){
                        str+=certs[i].subject.cn+","
                    }
                    alert(str)
                },fail:function (e){
                    alert(e.message)
                }
            })
        }

        /*监听插入设备事件*/
        m.addEventListener({
            name: "insertDevice",
            callback: waitcallback
        })
        /*监听拔插设备事件*/
        m.addEventListener({
            name: "removeDevice",
            callback: waitcallback
        })
    },
    fail:function (e) {
        alert(e.message)
    }
})`
    showCode('code',code)
}

function waitKeyEventSeq(){
    var code=`var seq=new HebcaMiddleware.Seq()
seq.then(function (){
    /*创建中间件对象*/
    new HebcaMiddleware.Middleware({
        success:function (m){
            seq.resolve(m)
        },
        fail:function (e){
            seq.reject(e)
        }
    })
}).then(function (seq,m){
    /*事件回调函数定义*/
    function waitcallback(event,param){
        /*触发回调事件后，进行处理，这里演示获取当前所有证书信息*/
        m.getCerts({
            success:function (certs){
                var str=event+": "+param.deviceName+", certs: "
                for(var i=0;i<certs.length;i++){
                    str+=certs[i].subject.cn+","
                }
                alert(str)
            },
            fail:function (e){
                alert(e.message)
            }
        })
    }
    /*监听插入设备事件*/
    m.addEventListener({
        name: "insertDevice",
        callback: waitcallback
    })
    /*监听拔插设备事件*/
    m.addEventListener({
        name: "removeDevice",
        callback: waitcallback
    })
}).onerror(function (e){
    alert(e.message)
}).run()`
    showCode('code_seq',code)
}

function waitKeyEventPromise(){
    var code=`HebcaMiddlewareX.createMiddleware()
    .then(m=>{
        /*事件回调函数定义*/
        function waitcallback(event,param){
            /*触发回调事件后，进行处理，这里演示获取当前所有证书信息*/
            m.getCerts().then((certs)=>{
                var str=event+": "+param.deviceName+", certs: "
                for(var i=0;i<certs.length;i++){
                    str+=certs[i].subject.cn+","
                }
                alert(str)
            })
        }
        /*监听插入设备事件*/
        m.addEventListener({
            name: "insertDevice",
            callback: waitcallback
        })
        /*监听拔插设备事件*/
        m.addEventListener({
            name: "removeDevice",
            callback: waitcallback
        })
    }).catch(e=>{
        alert(e.message)
    })`
    showCode('code_promise',code)
}

function waitKeyEventAsync(){
    var code=`(async function(){
    try{
        let m=await HebcaMiddlewareX.createMiddleware()
        /*事件回调函数定义*/
        let waitcallback=async (event,param)=>{
            /*触发回调事件后，进行处理，这里演示获取当前所有证书信息*/
            let certs=await m.getCerts()
            var str=event+": "+param.deviceName+", certs: "
            for(var i=0;i<certs.length;i++){
                str+=certs[i].subject.cn+","
            }
            alert(str)
        }
        /*监听插入设备事件*/
        m.addEventListener({
            name: "insertDevice",
            callback: waitcallback
        })
        /*监听拔插设备事件*/
        m.addEventListener({
            name: "removeDevice",
            callback: waitcallback
        })
    }catch (e){
        alert(e.message)
    }
})()`
    showCode('code_async',code)
}
