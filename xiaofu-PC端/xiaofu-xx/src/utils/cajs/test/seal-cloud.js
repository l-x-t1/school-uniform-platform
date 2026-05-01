var sealCloudTestSuit = new TestSuit("数据签章(密码云)");

var context_cloud="default-cloud"

sealCloudTestSuit.AddTest({
    name: "创建签章环境",
    asyn: true,
    fun: function(testResult){
        test_createContext(testResult,context_cloud)
    }
});

sealCloudTestSuit.AddTest({
    name: "列举印章",
    asyn: true,
    fun: function(testResult){
        test_listSeal(testResult,context_cloud)
    }
});

sealCloudTestSuit.AddTest({
    name: "获取印章数据",
    asyn: true,
    fun: function(testResult){
        test_getSealB64(testResult,context_cloud)
    }
});

sealCloudTestSuit.AddTest({
    name: "获取印章信息",
    asyn: true,
    fun: function(testResult){
        test_getSealInfo(testResult,context_cloud)
    }
});

sealCloudTestSuit.AddTest({
    name: "整体签章和验证",
    asyn: true,
    fun: function(testResult){
        test_signSealSignature(testResult,context_cloud)
    }
});

sealCloudTestSuit.AddTest({
    name: "整体签章和验证-批量",
    asyn: true,
    fun: function(testResult){
        test_signSealSignatureBatch(testResult,context_cloud)
    }
});

sealCloudTestSuit.AddTest({
    name: "分散签章和验证",
    asyn: true,
    fun: function(testResult){
        test_signSeal(testResult,context_cloud)
    }
});

sealCloudTestSuit.AddTest({
    name: "分散签章和验证-批量",
    asyn: true,
    fun: function(testResult){
        test_signSealBatch(testResult,context_cloud)
    }
});

sealCloudTestSuit.AddTest({
    name: "验证证书(默认)",
    asyn: true,
    fun: function(testResult){
        test_verifyCert(testResult,context_cloud)
    }
});

sealCloudTestSuit.AddTest({
    name: "验证证书(local)",
    asyn: true,
    fun: function(testResult){
        test_verifyCert(testResult,context_cloud,"local")
    }
});

sealCloudTestSuit.AddTest({
    name: "验证证书(svs)",
    asyn: true,
    fun: function(testResult){
        test_verifyCert(testResult,context_cloud,"svs")
    }
});

sealCloudTestSuit.AddTest({
    name: "验证证书(sealService)",
    asyn: true,
    fun: function(testResult){
        test_verifyCert(testResult,context_cloud,"sealService")
    }
});

sealCloudTestSuit.AddTest({
    name: "验证印章(默认)",
    asyn: true,
    fun: function(testResult){
        test_verifySeal(testResult,context_cloud)
    }
});

sealCloudTestSuit.AddTest({
    name: "验证印章(local)",
    asyn: true,
    fun: function(testResult){
        test_verifySeal(testResult,context_cloud,"local","local")
    }
});



allTestes.AddTestSuit(sealCloudTestSuit);

