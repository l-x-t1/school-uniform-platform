var sealComposeTestSuit = new TestSuit("数据签章(混合环境)");

var context_compose="default-compose"

sealComposeTestSuit.AddTest({
    name: "创建签章环境",
    asyn: true,
    fun: function(testResult){
        test_createContext(testResult,context_compose)
    }
});

sealComposeTestSuit.AddTest({
    name: "列举印章",
    asyn: true,
    fun: function(testResult){
        test_listSeal(testResult,context_compose)
    }
});

sealComposeTestSuit.AddTest({
    name: "获取印章数据",
    asyn: true,
    fun: function(testResult){
        test_getSealB64(testResult,context_compose)
    }
});

sealComposeTestSuit.AddTest({
    name: "获取印章信息",
    asyn: true,
    fun: function(testResult){
        test_getSealInfo(testResult,context_compose)
    }
});

sealComposeTestSuit.AddTest({
    name: "整体签章和验证",
    asyn: true,
    fun: function(testResult){
        test_signSealSignature(testResult,context_compose)
    }
});

sealComposeTestSuit.AddTest({
    name: "整体签章和验证-批量",
    asyn: true,
    fun: function(testResult){
        test_signSealSignatureBatch(testResult,context_compose)
    }
});

sealComposeTestSuit.AddTest({
    name: "分散签章和验证",
    asyn: true,
    fun: function(testResult){
        test_signSeal(testResult,context_compose)
    }
});

sealComposeTestSuit.AddTest({
    name: "分散签章和验证-批量",
    asyn: true,
    fun: function(testResult){
        test_signSealBatch(testResult,context_compose)
    }
});

allTestes.AddTestSuit(sealComposeTestSuit);
