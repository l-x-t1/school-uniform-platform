var monitorTestSuit = new TestSuit("模块管理");

monitorTestSuit.AddTest({
    name: "创建模块管理对象",
    asyn: true,
    fun: function(testResult){
        test_createMonitor(testResult)
    }
});

monitorTestSuit.AddTest({
    name: "获取当前版本",
    asyn: true,
    fun: function(testResult){
        monitor.getVersion({
            success:function (v){
                Trace.Info(JSON.stringify(v))
                testResult.done()
            },
            fail:function (e){
                testResult.fail(e.message)
            }
        })
    }
});

monitorTestSuit.AddTest({
    name: "检查服务状态为运行中",
    asyn: true,
    fun: function(testResult){
        var seq=new HebcaMiddleware.Seq();
        seq.then(function (){
            monitor.getServiceStatus({
                name:"middleware",
                success:function (s){
                    Assert.AssertEqualAsyn(testResult,"running",s[0].status,"middleware服务状态")
                    seq.resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (){
            monitor.getServiceStatus({
                name:"fileseal",
                success:function (s){
                    Assert.AssertEqualAsyn(testResult,"running",s[0].status,"fileseal服务状态")
                    seq.resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (){
            monitor.getServiceStatus({
                success:function (s){
                    for(var i=0;i<s.length;i++){
                        Assert.AssertEqualAsyn(testResult,"running",s[i].status,s[i].name+"服务状态")
                    }
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

monitorTestSuit.AddTest({
    name: "关闭服务middleware",
    asyn: true,
    fun: function(testResult){
        test_setService(testResult,"middleware","stop","stopped")
    }
});

monitorTestSuit.AddTest({
    name: "关闭服务fileseal",
    asyn: true,
    fun: function(testResult){
        test_setService(testResult,"fileseal","stop","stopped")
    }
});

monitorTestSuit.AddTest({
    name: "启动服务middleware",
    asyn: true,
    fun: function(testResult){
        test_setService(testResult,"middleware","start","running")
    }
});

monitorTestSuit.AddTest({
    name: "启动服务fileseal",
    asyn: true,
    fun: function(testResult){
        test_setService(testResult,"fileseal","start","running")
    }
});

monitorTestSuit.AddTest({
    name: "重启服务middleware",
    asyn: true,
    fun: function(testResult){
        test_setService(testResult,"middleware","restart","running")
    }
});

monitorTestSuit.AddTest({
    name: "重启服务fileseal",
    asyn: true,
    fun: function(testResult){
        test_setService(testResult,"fileseal","restart","running")
    }
});

monitorTestSuit.AddTest({
    name: "检查完整性",
    asyn: true,
    fun: function(testResult){
        monitor.checkDigest({
            success:function (m){
                Assert.AssertAsyn(testResult,m==null || m.length===0,"完整性")
                testResult.done()
            },
            fail:function (e){
                testResult.fail(e.message)
            }
        })
    }
});

monitorTestSuit.AddTest({
    name: "检查模块",
    asyn: true,
    fun: function(testResult){
        forEachMoudles(testResult,function (moudle,success,fail){
            monitor.checkMoudle({
                name: moudle.name,
                success: function (r){
                    if(r.result==="OK"){
                        Trace.Info("moudle "+moudle.name+" "+r.result)
                        success()
                    }else{
                        fail(new Error(r.message))
                    }
                },
                fail: function (e){
                    fail(e.message)
                }
            })
        })
    }
});


monitorTestSuit.AddTest({
    name: "修复模块middleware-下载并安装",
    asyn: true,
    fun: function(testResult){
        test_repairMoudle(testResult,"middleware","DOWNLOAD")
    }
});


monitorTestSuit.AddTest({
    name: "修复模块middleware-仅安装",
    asyn: true,
    fun: function(testResult){
        test_repairMoudle(testResult,"middleware","INSTALL")
    }
});

monitorTestSuit.AddTest({
    name: "修复模块middleware-仅重启",
    asyn: true,
    fun: function(testResult){
        test_repairMoudle(testResult,"middleware","RESTART")
    }
});

monitorTestSuit.AddTest({
    name: "检查COM组件",
    asyn: true,
    fun: function(testResult){

        monitor.checkCom({
            name:"HebcaP11X.CertMgr",
            success:function (){
                testResult.done()
            },
            fail:function (e){
                testResult.fail(e.message)
            }
        })
    }
});

monitorTestSuit.AddTest({
    name: "重启模块管理程序",
    asyn: true,
    fun: function(testResult){
        var seq=new HebcaMiddleware.Seq();
        seq.then(function (){
            monitor.restartMonitor({
                success:function (){
                    seq.resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (){
            //重新连接websocket
            function reConnect(){
                new HebcaMiddleware.Monitor({
                    disableTryStart: true,
                    success:function (m) {
                        monitor=m
                        seq.resolve()
                    },
                    fail:function (e) {
                        //重试
                        setTimeout(reConnect,2000)
                    }
                });
            }

            setTimeout(reConnect,5000)
        }).then(function (){
            monitor.getVersion({
                success:function (v){
                    Trace.TraceLn(JSON.stringify(v))
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



allTestes.AddTestSuit(monitorTestSuit);
