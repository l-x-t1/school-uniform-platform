
var monitor
function test_createMonitor(testResult){
    new HebcaMiddleware.Monitor({
        success:function (m){
            monitor=m
            testResult.done()
        },
        fail:function (e){
            testResult.fail(e.message)
        }
    });
}

function forEachMoudles(testResult,callback){
    monitor.getVersion({
        success:function (v){
            var seq=new HebcaMiddleware.Seq();
            seq.thenEach(v.moudles,function (seq,index) {
                callback(v.moudles[index],function () {
                    seq.resolve(index+1);
                },function (e) {
                    seq.reject(e);
                });
            }).then(function () {
                testResult.done();
            })
            .onerror(function (e) {
                testResult.fail(e.message);
            })
            .run(0)
        },
        fail:function (e){
            testResult.fail(e.message)
        }
    })
}


function test_setService(testResult,name,action,resultStatus){
    var seq=new HebcaMiddleware.Seq();
    seq.then(function (){
        monitor.setService({
            name:name,
            action:action,
            success:function (){
                seq.resolve()
            },
            fail:function (e){
                seq.reject(e)
            }
        })
    }).then(function (){
        monitor.getServiceStatus({
            name:"middleware",
            success:function (s){
                Assert.AssertEqualAsyn(testResult,resultStatus,s[0].status,name+"服务状态")
                seq.resolve()
            },
            fail:function (e){
                seq.reject(e)
            }
        })
    }).then(function (){
        testResult.done()
    }).onerror(function (e){
        testResult.fail(e.message)
    }).run()
}

function test_repairMoudle(testResult,name,type){
    var seq=new HebcaMiddleware.Seq();
    seq.then(function (){
        monitor.repairMoudle({
            name: name,
            type: type,
            success: function (){
                seq.resolve()
            },
            fail: function (e){
                seq.reject(e)
            }
        })
    }).then(function (){
        monitor.checkMoudle({
            name: name,
            success: function (r){
                if(r.result==="OK"){
                    Trace.Info("moudle "+name+" "+r.result)
                    testResult.done()
                }else{
                    seq.reject(new Error(r.message))
                }
            },
            fail: function (e){
                seq.reject(e)
            }
        })
    }).onerror(function (e){
        testResult.fail(e.message)
    }).run()
}
