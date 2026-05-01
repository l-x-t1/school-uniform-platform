var sealStandAloneTestSuit = new TestSuit("数据签章(单机版)");

var context_stand_alone="default-stand-alone"

sealStandAloneTestSuit.AddTest({
    name: "创建签章环境",
    asyn: true,
    fun: function(testResult){
        test_createContext(testResult,context_stand_alone)
    }
});

sealStandAloneTestSuit.AddTest({
    name: "列举印章",
    asyn: true,
    fun: function(testResult){
        test_listSeal(testResult,context_stand_alone)
    }
});

sealStandAloneTestSuit.AddTest({
    name: "获取印章数据",
    asyn: true,
    fun: function(testResult){
        test_getSealB64(testResult,context_stand_alone)
    }
});

sealStandAloneTestSuit.AddTest({
    name: "获取印章信息",
    asyn: true,
    fun: function(testResult){
        test_getSealInfo(testResult,context_stand_alone)
    }
});

sealStandAloneTestSuit.AddTest({
    name: "整体签章和验证",
    asyn: true,
    fun: function(testResult){
        test_signSealSignature(testResult,context_stand_alone)
    }
});

sealStandAloneTestSuit.AddTest({
    name: "整体签章和验证-批量",
    asyn: true,
    fun: function(testResult){
        test_signSealSignatureBatch(testResult,context_stand_alone)
    }
});

sealStandAloneTestSuit.AddTest({
    name: "分散签章和验证",
    asyn: true,
    fun: function(testResult){
        test_signSeal(testResult,context_stand_alone)
    }
});

sealStandAloneTestSuit.AddTest({
    name: "分散签章和验证-批量",
    asyn: true,
    fun: function(testResult){
        test_signSealBatch(testResult,context_stand_alone)
    }
});

sealStandAloneTestSuit.AddTest({
    name: "验证证书（默认）",
    asyn: true,
    fun: function(testResult){
        test_verifyCert(testResult,context_stand_alone)
    }
});

sealStandAloneTestSuit.AddTest({
    name: "验证证书（svs）",
    asyn: true,
    fun: function(testResult){
        test_verifyCert(testResult,context_stand_alone,"svs")
    }
});

sealStandAloneTestSuit.AddTest({
    name: "验证证书（sealService）",
    asyn: true,
    fun: function(testResult){
        test_verifyCert(testResult,context_stand_alone,"sealService")
    }
});

sealStandAloneTestSuit.AddTest({
    name: "验证证书（cloud）",
    asyn: true,
    fun: function(testResult){
        test_verifyCert(testResult,context_stand_alone,"cloud")
    }
});

sealStandAloneTestSuit.AddTest({
    name: "验证印章（默认）",
    asyn: true,
    fun: function(testResult){
        test_verifySeal(testResult,context_stand_alone)
    }
});

/*
sealStandAloneTestSuit.AddTest({
    name: "验证印章（证书sealService,印章sealService）",
    asyn: true,
    fun: function(testResult){
        test_verifySeal(testResult,context_stand_alone,"sealService","sealService")
    }
});

sealStandAloneTestSuit.AddTest({
    name: "验证印章（证书cloud，印章cloud）",
    asyn: true,
    fun: function(testResult){
        test_verifySeal(testResult,context_stand_alone,"cloud","cloud")
    }
});
 */


allTestes.AddTestSuit(sealStandAloneTestSuit);
