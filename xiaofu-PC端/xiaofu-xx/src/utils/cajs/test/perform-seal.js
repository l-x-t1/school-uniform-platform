var performSealTestSuit = new TestSuit("数据签章性能");

var times_signSealSignature=100

function perform_signSealSignature(testResult,contextName,times){
    firstSeal(testResult,contextName,function (seal,resolve,reject){
        var seq=new HebcaMiddleware.Seq();
        var tt=[]
        for(var i=0;i<times;i++){
            tt.push(i)
        }
        var startTime;
        var endTime;
        startTime=new Date()
        seq.thenEach(tt,function (seq,index){
            sealContext[contextName].signSealSignature({
                sealSn: seal.sealSn,
                data: "123456",
                show: {
                    divId: "seal_show",
                    ui: HebcaMiddleware.uiBootstrap,
                },

                success:function (sealSignature){
                    Assert.AssertAsyn(testResult,sealSignature.length>0,"整体签章")
                    Trace.TraceLn(contextName+"-整体签章-"+index)
                    seq.resolve(index+1)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (){
            endTime=new Date()
            var ms=endTime - startTime;   // 毫秒数
            Trace.TraceLn(contextName+"-"+times+"次-共执行"+(ms/1000)+"秒,约"+((ms/1000)/times)+"秒/次")
            testResult.done()
        }).onerror(function (e){
            reject(e)
        }).run(0)
    })
}

function perform_signSealSignatureBatch(testResult,contextName,times,enableThreads){
    firstSeal(testResult,contextName,function (seal,resolve,reject){
        var seq=new HebcaMiddleware.Seq();
        var data=[]
        for(var i=0;i<times;i++){
            data.push("123456")
        }
        var startTime;
        var endTime;
        startTime=new Date()
        seq.then(function (){
            sealContext[contextName].signSealSignatureBatch({
                sealSn: seal.sealSn,
                data: data,
                enableThreads: enableThreads,
                success:function (sealSignature){
                    Assert.AssertAsyn(testResult,sealSignature.length===times,"整体签章")
                    seq.resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (){
            endTime=new Date()
            var ms=endTime - startTime;   // 毫秒数
            Trace.TraceLn(contextName+"-整体签章批量-共执行"+(ms/1000)+"秒,约"+((ms/1000)/times)+"秒/次")
            testResult.done()
        }).onerror(function (e){
            reject(e)
        }).run(0)
    })
}

function perform_signSeal(testResult,contextName,times){
    firstSeal(testResult,contextName,function (seal,resolve,reject){
        var seq=new HebcaMiddleware.Seq();
        var tt=[]
        for(var i=0;i<times;i++){
            tt.push(i)
        }
        var startTime;
        var endTime;
        startTime=new Date()
        seq.thenEach(tt,function (seq,index){
            sealContext[contextName].signSeal({
                sealSn: seal.sealSn,
                data: "123456",
                show: {
                    divId: "seal_show",
                    ui: HebcaMiddleware.uiBootstrap,
                },
                success:function (result){
                    Trace.TraceLn(contextName+"-分散签章-"+index)
                    seq.resolve(index+1)
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (){
            endTime=new Date()
            var ms=endTime - startTime;   // 毫秒数
            Trace.TraceLn(contextName+"-"+times+"次-共执行"+(ms/1000)+"秒,约"+((ms/1000)/times)+"秒/次")
            testResult.done()
        }).onerror(function (e){
            reject(e)
        }).run(0)
    })
}

function perform_signSealBatch(testResult,contextName,times,enableThreads){
    firstSeal(testResult,contextName,function (seal,resolve,reject){
        var seq=new HebcaMiddleware.Seq();
        var data=[]
        for(var i=0;i<times;i++){
            data.push("123456")
        }
        var startTime;
        var endTime;
        startTime=new Date()
        seq.then(function (){
            sealContext[contextName].signSealBatch({
                sealSn: seal.sealSn,
                data: data,
                enableThreads:enableThreads,
                success:function (result){
                    Assert.AssertAsyn(testResult,result.length===times,"分散签章")
                    seq.resolve()
                },
                fail:function (e){
                    seq.reject(e)
                }
            })
        }).then(function (){
            endTime=new Date()
            var ms=endTime - startTime;   // 毫秒数
            Trace.TraceLn(contextName+"-分散签章批量-共执行"+(ms/1000)+"秒,约"+((ms/1000)/times)+"秒/次")
            testResult.done()
        }).onerror(function (e){
            reject(e)
        }).run(0)
    })
}

function perform_createContext(name,success,fail){
    new HebcaMiddleware.Seal({
        context: name,
        success:function (s){
            sealContext[name]=s
            success()
        },
        fail:function (e){
            fail(e)
        }
    });
}

performSealTestSuit.AddTest({
    name: "初始化签章环境",
    asyn: true,
    fun: function (testResult) {
        var seq=new HebcaMiddleware.Seq();
        seq.then(function (){
            perform_createContext(context_stand_alone,function (){
                seq.resolve()
            },function (e){
                seq.reject(e)
            })
        }).then(function (){
            perform_createContext(context_net,function (){
                seq.resolve()
            },function (e){
                seq.reject(e)
            })
        }).then(function (){
            perform_createContext(context_cloud,function (){
                seq.resolve()
            },function (e){
                seq.reject(e)
            })
        }).then(function (){
            testResult.done()
        }).onerror(function (e){
            testResult.fail(e.message)
        }).run()
    }
});

performSealTestSuit.AddTest({
    name: "整体签章-单机版-"+times_signSealSignature+"次",
    asyn: true,
    fun: function (testResult) {
        perform_signSealSignature(testResult,context_stand_alone,times_signSealSignature)
    }
});

performSealTestSuit.AddTest({
    name: "整体签章-网络版-"+times_signSealSignature+"次",
    asyn: true,
    fun: function (testResult) {
        perform_signSealSignature(testResult,context_net,times_signSealSignature)
    }
});

performSealTestSuit.AddTest({
    name: "整体签章-密码云版-"+times_signSealSignature+"次",
    asyn: true,
    fun: function (testResult) {
        perform_signSealSignature(testResult,context_cloud,times_signSealSignature)
    }
});

performSealTestSuit.AddTest({
    name: "整体签章批量-单机版-"+times_signSealSignature+"条",
    asyn: true,
    fun: function (testResult) {
        perform_signSealSignatureBatch(testResult,context_stand_alone,times_signSealSignature,false)
    }
});

performSealTestSuit.AddTest({
    name: "整体签章批量-网络版-"+times_signSealSignature+"条",
    asyn: true,
    fun: function (testResult) {
        perform_signSealSignatureBatch(testResult,context_net,times_signSealSignature,false)
    }
});

performSealTestSuit.AddTest({
    name: "整体签章批量-密码云版-"+times_signSealSignature+"条",
    asyn: true,
    fun: function (testResult) {
        perform_signSealSignatureBatch(testResult,context_cloud,times_signSealSignature,false)
    }
});

performSealTestSuit.AddTest({
    name: "整体签章批量-单机版-多线程-"+times_signSealSignature+"条",
    asyn: true,
    fun: function (testResult) {
        perform_signSealSignatureBatch(testResult,context_stand_alone,times_signSealSignature,true)
    }
});

performSealTestSuit.AddTest({
    name: "整体签章批量-网络版-多线程-"+times_signSealSignature+"条",
    asyn: true,
    fun: function (testResult) {
        perform_signSealSignatureBatch(testResult,context_net,times_signSealSignature,true)
    }
});

performSealTestSuit.AddTest({
    name: "整体签章批量-密码云版-多线程-"+times_signSealSignature+"条",
    asyn: true,
    fun: function (testResult) {
        perform_signSealSignatureBatch(testResult,context_cloud,times_signSealSignature,true)
    }
});

performSealTestSuit.AddTest({
    name: "分散签章-单机版-"+times_signSealSignature+"次",
    asyn: true,
    fun: function (testResult) {
        perform_signSeal(testResult,context_stand_alone,times_signSealSignature)
    }
});

performSealTestSuit.AddTest({
    name: "分散签章-网络版-"+times_signSealSignature+"次",
    asyn: true,
    fun: function (testResult) {
        perform_signSeal(testResult,context_net,times_signSealSignature)
    }
});

performSealTestSuit.AddTest({
    name: "分散签章-密码云版-"+times_signSealSignature+"次",
    asyn: true,
    fun: function (testResult) {
        perform_signSeal(testResult,context_cloud,times_signSealSignature)
    }
});

performSealTestSuit.AddTest({
    name: "分散签章批量-单机版-"+times_signSealSignature+"条",
    asyn: true,
    fun: function (testResult) {
        perform_signSealBatch(testResult,context_stand_alone,times_signSealSignature,false)
    }
});

performSealTestSuit.AddTest({
    name: "分散签章批量-网络版-"+times_signSealSignature+"条",
    asyn: true,
    fun: function (testResult) {
        perform_signSealBatch(testResult,context_net,times_signSealSignature,false)
    }
});

performSealTestSuit.AddTest({
    name: "分散签章批量-密码云版-"+times_signSealSignature+"条",
    asyn: true,
    fun: function (testResult) {
        perform_signSealBatch(testResult,context_cloud,times_signSealSignature,false)
    }
});

performSealTestSuit.AddTest({
    name: "分散签章批量-单机版-多线程-"+times_signSealSignature+"条",
    asyn: true,
    fun: function (testResult) {
        perform_signSealBatch(testResult,context_stand_alone,times_signSealSignature,true)
    }
});

performSealTestSuit.AddTest({
    name: "分散签章批量-网络版-多线程-"+times_signSealSignature+"条",
    asyn: true,
    fun: function (testResult) {
        perform_signSealBatch(testResult,context_net,times_signSealSignature,true)
    }
});

performSealTestSuit.AddTest({
    name: "分散签章批量-密码云版-多线程-"+times_signSealSignature+"条",
    asyn: true,
    fun: function (testResult) {
        perform_signSealBatch(testResult,context_cloud,times_signSealSignature,true)
    }
});



allTestes.AddTestSuit(performSealTestSuit);