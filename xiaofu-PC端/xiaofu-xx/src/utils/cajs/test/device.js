var deviceTestSuit = new TestSuit("设备接口");

deviceTestSuit.AddTest({
    name: "获取设备列表",
    asyn: true,
    fun: function (testResult) {
        m.getDevices({
            success:function (devices){
                Trace.TraceLn(JSON.stringify(devices));
                testResult.done();
            },
            fail:function (e) {
                testResult.fail(e.message);
            }
        })
    }
});

function forEachDevice(testResult,callback){
    m.getDevices({
        success:function (devices) {
            var seq=new HebcaMiddleware.Seq();
            seq.thenEach(devices,function (seq,index) {
                callback(devices[index],function () {
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
        fail:function (e) {
            testResult.fail(e.message)
        }
    })
}

deviceTestSuit.AddTest({
    name: "获取随机数",
    asyn: true,
    fun: function (testResult) {
        forEachDevice(testResult,function (device,resovle,reject) {
            m.getRandom({
                deviceId: device.deviceId,
                randomLength: 1024,
                success:function (random){
                    Trace.TraceLn("random: "+random)
                    Assert.AssertAsyn(testResult,random.length>1024, "获取随机数");
                    resovle();
                },
                fail:function (e){
                    reject(e)
                }
            })
        });
    }
});

deviceTestSuit.AddTest({
    name: "获取设备信息",
    asyn: true,
    fun: function (testResult) {
        forEachDevice(testResult,function (device,resovle,reject) {
            m.getDeviceInfo({
                deviceId: device.deviceId,
                success:function (info){
                    Trace.TraceLn(JSON.stringify(info))
                    Assert.AssertEqualAsyn(testResult,device.deviceId,info.deviceId,"设备信息：deviceId");
                    Assert.AssertEqualAsyn(testResult,device.deviceName,info.deviceName,"设备信息：deviceName");
                    //Assert.AssertAsyn(testResult,info.freeSpace<=info.TotalSpace, "设备信息：设备空间大小");
                    resovle();
                },
                fail:function (e){
                    reject(e)
                }
            })
        });
    }
});

deviceTestSuit.AddTest({
    name: "设备数据读取",
    asyn: true,
    fun: function (testResult) {
        var cur_datas
        var cur_cert
        forEachSignCert(testResult,function (cert,resovle,reject) {
            var seq=new HebcaMiddleware.Seq()
            seq.then(function (){
                cur_cert=cert
                m.getDatas({
                    sn: cert.sn,
                    success:function (datas) {
                        Trace.TraceLn(JSON.stringify(datas));
                        cur_datas=datas
                        var seq2=new HebcaMiddleware.Seq()
                        seq2.thenEach(cur_datas,function (seq,index){
                            m.readData({
                                sn: cur_cert.sn,
                                name: cur_datas[index].name,
                                dataFormat: 'Base64',
                                success: function (data){
                                    Trace.TraceLn("Data - "+cur_datas[index].name+": "+data)
                                    seq2.resolve(index+1)
                                },
                                fail: function (e) {
                                    seq2.reject(e);
                                }
                            })
                        }).then(function (){
                            resovle();
                        })
                            .onerror(function (e){
                                reject(e)
                            })
                            .run(0)

                    },
                    fail: function (e) {
                        seq.reject(e);
                    }
                })
            })
                .onerror(function (e){
                reject(e)
            })
                .run()

        });
    }
});


deviceTestSuit.AddTest({
    name: "设备数据写入和删除",
    asyn: true,
    fun: function (testResult) {
        var cur_datas
        var cur_cert
        forEachSignCert(testResult,function (cert,resovle,reject) {
            var seq=new HebcaMiddleware.Seq()
            seq.then(function (){
                cur_cert=cert
                m.getDatas({
                    sn: cert.sn,
                    success:function (datas) {
                        cur_datas=datas
                        seq.resolve()
                    },
                    fail: function (e) {
                        seq.reject(e);
                    }
                })
            }).then(function (){
                m.writeData({
                    sn: cert.sn,
                    name: 'test123',
                    data: '12345678',
                    success:function () {
                        seq.resolve()
                    },
                    fail: function (e) {
                        seq.reject(e);
                    }
                })
            }).then(function (){
                m.readData({
                    sn: cert.sn,
                    name: 'test123',
                    success:function (data) {
                        Assert.AssertEqualAsyn(testResult,'12345678',data,"读取数据");
                        seq.resolve()
                    },
                    fail: function (e) {
                        seq.reject(e);
                    }
                })
            })
            .then(function (){
                m.deleteData({
                    sn: cert.sn,
                    name: 'test123',
                    success:function () {
                        seq.resolve()
                    },
                    fail: function (e) {
                        seq.reject(e);
                    }
                })
            })
                .then(function (){
                    m.getDatas({
                        sn: cert.sn,
                        success:function (datas2) {
                            Assert.AssertEqualAsyn(testResult,cur_datas.length,datas2.length,"数据个数保持不变");
                            seq.resolve()
                        },
                        fail: function (e) {
                            seq.reject(e);
                        }
                    })
                })
                .then(function (){
                resovle()
            }).onerror(function (e){
                    reject(e)
            }).run()

        });
    }
});

allTestes.AddTestSuit(deviceTestSuit);
