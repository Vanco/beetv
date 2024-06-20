package vanstudio.tv.biliapi.http.entity.index

object IndexFilterStyle {
    private val styleFilter = mapOf(
        -1 to "全部",
        -10 to "电影",

        10010 to "原创",
        10011 to "漫画改",
        10012 to "小说改",
        10013 to "游戏改",
        10014 to "动态漫",
        10015 to "布袋戏",
        10016 to "热血",
        10017 to "穿越",
        10018 to "奇幻",
        10019 to "玄幻",

        10020 to "战斗",
        10021 to "搞笑",
        10022 to "日常",
        10023 to "科幻",
        10024 to "萌系",
        10025 to "治愈",
        10026 to "校园",
        10027 to "少儿",
        10028 to "泡面",
        10029 to "恋爱",

        10030 to "少女",
        10031 to "魔法",
        10032 to "冒险",
        10033 to "历史",
        10034 to "架空",
        10035 to "机战",
        10036 to "神魔",
        10037 to "声控",
        10038 to "运动",
        10039 to "励志",

        10040 to "音乐",
        10041 to "推理",
        10042 to "社团",
        10043 to "智斗",
        10044 to "催泪",
        10045 to "美食",
        10046 to "偶像",
        10047 to "乙女",
        10048 to "职场",
        10049 to "古风",

        10050 to "剧情",
        10051 to "喜剧",
        10052 to "爱情",
        10053 to "动作",
        10054 to "恐怖",
        10055 to "犯罪",
        10056 to "惊悚",
        10057 to "悬疑",
        10058 to "战争",

        10061 to "家庭",
        10064 to "灾难",
        10065 to "人文",
        10066 to "科技",
        10067 to "探险",
        10068 to "宇宙",
        10069 to "萌宠",

        10070 to "社会",
        10071 to "动物",
        10072 to "自然",
        10073 to "医疗",
        10074 to "军事",
        10075 to "罪案",
        10076 to "神秘",
        10077 to "旅行",
        10078 to "武侠",
        10079 to "青春",

        10080 to "都市",
        10081 to "古装",
        10082 to "谍战",
        10083 to "经典",
        10084 to "情感",
        10085 to "神话",
        10086 to "年代",
        10087 to "农村",
        10088 to "刑侦",
        10089 to "军旅",

        10090 to "访谈",
        10091 to "脱口秀",
        10092 to "真人秀",
        10094 to "选秀",
        10095 to "旅游",
        10096 to "演唱会",
        10097 to "亲子",
        10098 to "晚会",
        10099 to "养成",

        10100 to "文化",
        10102 to "特摄",
        10103 to "短剧",
        10104 to "短片",
    )

    private val animeStyleIds = listOf(
        -1, 10010, 10011, 10012, 10013, 10102, 10015, 10016, 10017, 10018,
        10020, 10021, 10022, 10023, 10024, 10025, 10026, 10027, 10028, 10029,
        10030, 10031, 10032, 10033, 10034, 10035, 10036, 10037, 10038, 10039,
        10040, 10041, 10042, 10043, 10044, 10045, 10046, 10047, 10048
    )
    private val guochuangStyleIds = listOf(
        -1, 10010, 10011, 10012, 10013, 10102, 10015, 10016, 10018, 10019,
        10020, 10021, 10078, 10022, 10023, 10024, 10025, 10057, 10026, 10027,
        10028, 10029, 10030, 10031, 10033, 10035, 10036, 10037, 10038, 10039,
        10040, 10041, 10042, 10043, 10044, 10045, 10046, 10047, 10048, 10049
    )
    private val varietyStyleIds = listOf(
        -1, 10040, 10090, 10091, 10092, 10094, 10045, 10095, 10098, 10096,
        10084, 10051, 10097, 10100, 10048, 10069, 10099
    )
    private val movieStyleIds = listOf(
        -1, 10104, 10050, 10051, 10052, 10053, 10054, 10023, 10055, 10056,
        10057
    )
    private val tvStyleIds = listOf(
        -1, 10021, 10018, 10058, 10078, 10079, 10103, 10080, 10081, 10082,
        10083, 10084, 10057, 10039, 10085, 10017, 10086, 10087, 10088, 10050,
        10061, 10033, 10089, 10023,
    )
    private val documentaryStyleIds = listOf(
        -1, 10033, 10045, 10065, 10066, 10067, 10068, 10069, 10070, 10071,
        10072, 10073, 10074, 10064, 10075, 10076, 10077, 10038, -10,
    )

    val animeStyles by lazy { animeStyleIds.associateWith { styleFilter[it]!! } }
    val guochuangStyles by lazy { guochuangStyleIds.associateWith { styleFilter[it]!! } }
    val varietyStyles by lazy { varietyStyleIds.associateWith { styleFilter[it]!! } }
    val movieStyles by lazy { movieStyleIds.associateWith { styleFilter[it]!! } }
    val tvStyles by lazy { tvStyleIds.associateWith { styleFilter[it]!! } }
    val documentaryStyles by lazy { documentaryStyleIds.associateWith { styleFilter[it]!! } }
}