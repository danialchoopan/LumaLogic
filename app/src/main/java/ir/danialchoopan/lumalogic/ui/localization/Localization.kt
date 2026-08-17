package ir.danialchoopan.lumalogic.ui.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import ir.danialchoopan.lumalogic.data.level.LevelRegistry
import ir.danialchoopan.lumalogic.data.model.Achievement
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.Chapter
import ir.danialchoopan.lumalogic.data.model.GateType
import ir.danialchoopan.lumalogic.data.model.Level
import ir.danialchoopan.lumalogic.data.model.LightColor
import ir.danialchoopan.lumalogic.domain.hint.Hint
import ir.danialchoopan.lumalogic.domain.hint.HintType

/**
 * Converts ASCII digits 0-9 to Persian digits when isPersian is true.
 */
fun String.toPersianDigits(isPersian: Boolean): String {
    if (!isPersian) return this
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val sb = StringBuilder()
    for (ch in this) {
        if (ch in '0'..'9') {
            sb.append(persianDigits[ch - '0'])
        } else {
            sb.append(ch)
        }
    }
    return sb.toString()
}

/**
 * Helper class for runtime localized string lookups and text translation.
 */
class LocalizationManager(val isPersian: Boolean) {

    fun formatNumber(num: Int): String {
        return num.toString().toPersianDigits(isPersian)
    }

    fun formatNumber(num: Long): String {
        return num.toString().toPersianDigits(isPersian)
    }

    fun formatNumber(num: Float): String {
        return "%.1f".format(num).toPersianDigits(isPersian)
    }

    fun getDifficultyLabel(diff: String): String {
        if (!isPersian) return diff
        return when (diff.uppercase()) {
            "ALL" -> "همه"
            "FAVORITES" -> "علاقه‌مندی‌ها"
            "BEGINNER" -> "مبتدی"
            "EASY" -> "آسان"
            "NORMAL", "MEDIUM" -> "معمولی"
            "HARD" -> "سخت"
            "EXPERT" -> "پیشرفته"
            "MASTER" -> "استاد"
            else -> diff
        }
    }

    fun getComponentName(type: CellType?): String {
        if (!isPersian) return type?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Component"
        return when (type) {
            CellType.MIRROR -> "آینه"
            CellType.SPLITTER -> "منشور شکافنده"
            CellType.FILTER -> "فیلتر نوری"
            CellType.GATE -> "دروازه منطقی"
            CellType.WIRE -> "سیم نوری"
            CellType.SOURCE -> "منبع نور"
            CellType.TARGET -> "هدف گیرنده"
            CellType.BLOCK -> "مانع"
            CellType.EMPTY, null -> "خانه خالی"
        }
    }

    fun getColorName(color: LightColor?): String {
        if (!isPersian) return color?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "White"
        return when (color) {
            LightColor.RED -> "قرمز"
            LightColor.BLUE -> "آبی"
            LightColor.GREEN -> "سبز"
            LightColor.YELLOW -> "زرد"
            LightColor.WHITE, null -> "سفید"
        }
    }

    fun getGateName(gateType: GateType?): String {
        if (!isPersian) return gateType?.name ?: "Logic"
        return when (gateType) {
            GateType.AND -> "AND (و)"
            GateType.OR -> "OR (یا)"
            GateType.NOT -> "NOT (نقیض)"
            null -> "منطقی"
        }
    }

    fun formatHint(hint: Hint): String {
        if (!isPersian) return hint.message

        val rowStr = hint.position?.let { (it.row + 1).toString().toPersianDigits(true) } ?: ""
        val colStr = hint.position?.let { (it.column + 1).toString().toPersianDigits(true) } ?: ""
        val toRowStr = hint.targetPosition?.let { (it.row + 1).toString().toPersianDigits(true) } ?: ""
        val toColStr = hint.targetPosition?.let { (it.column + 1).toString().toPersianDigits(true) } ?: ""
        val compName = getComponentName(hint.cellType)

        return when (hint.type) {
            HintType.ROTATE -> {
                "قطعه $compName در سطر $rowStr، ستون $colStr را بچرخانید تا مسیر پرتو نور تنظیم شود."
            }
            HintType.MOVE -> {
                "قطعه $compName را از سطر $rowStr، ستون $colStr به سطر $toRowStr، ستون $toColStr منتقل کنید."
            }
            HintType.PATH -> {
                "مسیر پرتو نور را بررسی کنید تا بدون برخورد با موانع به گیرنده برسد."
            }
            HintType.COLOR -> {
                val colorName = getColorName(hint.color)
                "فیلتر در سطر $rowStr، ستون $colStr پرتو را مسدود کرده است. نور هم‌رنگ ($colorName) را به آن هدایت کنید."
            }
            HintType.GATE -> {
                val gateName = getGateName(hint.gateType)
                "دروازه منطقی $gateName در سطر $rowStr، ستون $colStr برای ساطع کردن نور به ورودی فعال نیاز دارد."
            }
            HintType.GENERAL -> {
                if (hint.message.contains("solved", ignoreCase = true)) {
                    "معما حل شده است! تمام اهداف فعال هستند."
                } else if (hint.position != null) {
                    "هدف در سطر $rowStr، ستون $colStr نوری دریافت نمی‌کند. پرتو را به این خانه هدایت کنید."
                } else {
                    "آینه‌ها و منشورها را بچرخانید تا پرتوهای نور به سمت اهداف گیرنده هدایت شوند."
                }
            }
        }
    }

    fun formatHintAction(hint: Hint): String? {
        if (!isPersian) return hint.suggestedAction

        val rowStr = hint.position?.let { (it.row + 1).toString().toPersianDigits(true) } ?: ""
        val colStr = hint.position?.let { (it.column + 1).toString().toPersianDigits(true) } ?: ""
        val toRowStr = hint.targetPosition?.let { (it.row + 1).toString().toPersianDigits(true) } ?: ""
        val toColStr = hint.targetPosition?.let { (it.column + 1).toString().toPersianDigits(true) } ?: ""
        val compName = getComponentName(hint.cellType)

        return when (hint.type) {
            HintType.ROTATE -> "چرخش $compName در ($rowStr, $colStr)"
            HintType.MOVE -> "انتقال $compName به ($toRowStr, $toColStr)"
            HintType.PATH -> "تنظیم مسیر پرتو"
            HintType.COLOR -> "تنظیم رنگ پرتو ورودی به فیلتر"
            HintType.GATE -> "تأمین نور ورودی برای دروازه منطقی"
            HintType.GENERAL -> "تنظیم مسیر پرتوها"
        }
    }

    fun getChapterName(chapter: Chapter): String {
        if (!isPersian) return chapter.name
        return when (chapter.number) {
            1 -> "پایه‌های نور"
            2 -> "بازتاب و زاویه‌ها"
            3 -> "دقت و محدودیت"
            4 -> "منشورهای شکافنده"
            5 -> "طیف رنگ‌ها"
            6 -> "فیلترهای کروماتیک"
            7 -> "صرفه‌جویی انرژی"
            8 -> "آرایه چند پرتو"
            9 -> "دروازه منطقی AND"
            10 -> "دروازه منطقی OR"
            11 -> "دروازه وارونه‌ساز NOT"
            12 -> "شبکه‌های منطقی"
            13 -> "مسیریابی پیشرفته"
            14 -> "استاد انرژی"
            15 -> "معمای پیشرفته"
            16 -> "استاد LumaLogic"
            else -> chapter.name
        }
    }

    fun getChapterDescription(chapter: Chapter): String {
        if (!isPersian) return chapter.description
        return when (chapter.number) {
            1 -> "آموزش اصول اولیه بازتاب نور و هدایت پرتوها به گیرنده‌ها"
            2 -> "استفاده از آینه‌های متقاطع برای هدایت نور در زوایای مختلف"
            3 -> "تکمیل معماها با کمترین حرکت و مصرف بهینه انرژی"
            4 -> "تقسیم پرتوهای اصلی به چند پرتو موازی برای فعال‌سازی چند هدف"
            5 -> "ترکیب و عبور نورهای قرمز، سبز و آبی در طیف‌های مختلف"
            6 -> "استفاده از فیلترهای رنگی برای جداسازی طول موج‌های مختلف"
            7 -> "مدیریت دقیق انرژی و برنامه‌ریزی چرخش آینه‌ها"
            8 -> "کنترل همزمان چند پرتو ورودی و خروجی در ماتریس‌های پیچیده"
            9 -> "استفاده از دروازه AND برای هم‌پوشانی پرتوها"
            10 -> "ایجاد مسیرهای جایگزین با استفاده از دروازه OR"
            11 -> "معکوس کردن حالات فعال و غیرفعال پرتو با دروازه NOT"
            12 -> "ترکیب چند دروازه منطقی برای حل مدارهای نوری پیچیده"
            13 -> "مسیریابی دقیق پرتوها در شبکه‌های پر از موانع"
            14 -> "حل معماهای بزرگ با محدودیت شدید انرژی"
            15 -> "چالش‌های نفس‌گیر برای سنجش تفکر منطقی"
            16 -> "آزمون نهایی مهارت‌های نوری و منطقی در ۲۵۶ مرحله"
            else -> chapter.description
        }
    }

    private val persianLevelNames: List<String> = listOf(
        // Chapter 1 (1..16)
        "نخستین درخشش", "پرتو خطی", "گوشه بازتابی", "زاویه دوگانه", "تنظیم منشور", "پرتو مستقیم", "آینه دوگانه", "جهش مورب", "مسیر نور", "دید هدف", "مسیر بازتابی", "جهت‌یابی شبکه", "ردپای فوتون", "ورود به لوما", "جاروب گوشه", "استاد مبانی",
        // Chapter 2 (17..32)
        "چرخش اول", "بازتاب مضاعف", "حلقه گوشه", "ماز آینه‌ای", "پرتو سه‌گانه", "دیوار بازتاب", "نقطه شکست", "نور زیگزاگی", "دروازه آینه", "مسیر جهش", "آینه‌های موازی", "جهش چهارگانه", "قفل زاویه", "راه انحراف", "کلید اپتیک", "استاد بازتاب",
        // Chapter 3 (33..48)
        "پیچ‌های تند", "حداقل حرکات", "راهروی طولانی", "مسیر باریک", "دقت جراحی", "رسیدن به هدف", "بهینه‌ساز گام", "پرتو مقید", "نقطه تقارن", "زاویه دقیق", "تعادل آینه", "پرتو حساب‌شده", "گام حیاتی", "سوزن اپتیک", "پرتو متمرکز", "استاد دقت",
        // Chapter 4 (49..64)
        "نخستین شکافت", "پرتوهای دوتایی", "انشعاب نور", "پرتوهای موازی", "شبکه شکافنده", "اهداف دوقلو", "تقسیم‌کننده پرتو", "مسیر شاخه", "آینه شکافنده", "پرتو متقاطع", "انشعاب چهارتایی", "شکافت T", "شاخه طیفی", "مدار انشعابی", "اتصال چندپرتو", "استاد شکافنده",
        // Chapter 5 (65..80)
        "سپیده سرخ", "پرتو لاجوردی", "فانوس سبز", "پل طیف", "میکسر رنگ", "تراز RGB", "مسیر کروما", "تغییر پایه", "تقاطع فام", "پرتو درخشان", "هدف سه‌رنگ", "مسیریابی رنگی", "موج طیفی", "دید منشوری", "هارمونی رنگ", "استاد رنگ‌ها",
        // Chapter 6 (81..96)
        "نخستین فیلتر", "سد قرمز", "عبور آبی", "دروازه سبز", "فیلتر کروما", "پرتو چندفیلتری", "شاخه فیلترشده", "انتخابگر رنگ", "ماز فیلتر", "سد طیفی", "رله فیلتر", "غربال رنگ", "فیلتر دوگانه", "نور میان‌گذر", "قفل فیلتر", "استاد فیلترها",
        // Chapter 7 (97..112)
        "نخستین انرژی", "پرتو مقتصد", "سلول انرژی", "کوتاه‌ترین مسیر", "محدودیت قدرت", "مسیر بهینه", "پرتو پایستگی", "پل انرژی", "اجرای کم‌مصرف", "زاویه کارآمد", "راهروی صرفه‌جویی", "دروازه انرژی", "رد کمینه", "ذخیره باتری", "بهینه‌سازی توان", "استاد انرژی",
        // Chapter 8 (113..128)
        "منابع دوقلو", "کانال دوگانه", "ماتریس چندپرتو", "پرتوهای متقاطع", "منابع موازی", "جریان متقاطع", "پرتو چندهدفه", "آرایه منبع", "پرتو چهارگانه", "تقاطع پیچیده", "واگرایی پرتو", "پل منبع", "منطق چندمنبعی", "طیف دوگانه", "سنتز پرتو", "استاد چندپرتو",
        // Chapter 9 (129..144)
        "نخستین همبستگی", "فعال‌سازی دوگانه", "شرط AND", "پالس منطقی", "پیوند AND", "کلید دوطرفه", "پرتو همگام", "پل دروازه AND", "سیگنال دوگانه", "تراز منطقی", "دوگانه الزامی", "ماز پیوند", "نور هم‌زمانی", "هدف دوورودی", "قفل منطق", "استاد AND",
        // Chapter 10 (145..160)
        "مسیر جایگزین", "راه مازاد", "مسیر دروازه OR", "کانال انعطاف‌پذیر", "مسیر دوگانه", "منطق موازی", "تقاطع OR", "پرتو پشتیبان", "انتخاب مسیر", "هدف اختیاری", "پرتو اضافی", "گزینش OR", "جریان دوگانه", "کلید متناوب", "مسیر OR", "استاد OR",
        // Chapter 11 (161..176)
        "نور وارونه", "شرط NOT", "وارون‌ساز منطقی", "پرتو معکوس", "مسیر وارونه", "کلید منطق", "پیوند NOT", "ماتریس وارونگی", "دروازه معکوس", "پرتو منفی", "مسیریابی معکوس", "سد NOT", "تغییر حالت منطق", "هدف معکوس", "شبکه NOT", "استاد NOT",
        // Chapter 12 (177..192)
        "نخستین شبکه", "آبشار منطق", "شبکه AND-OR", "پیوند وارونه", "توری فیلتر منطقی", "دروازه مرکب", "پرتو چندمنطقی", "مسیریاب شبکه", "ماتریس منطق", "دروازه آبشاری", "پرتوهای متصل", "توری منطقی", "مدار ترکیبی", "شبکه گیت", "سیستم همبستگی", "استاد شبکه منطق",
        // Chapter 13 (193..208)
        "شبکه بزرگ", "مسیر مرکب", "چالش مسیریابی", "ناوبری ماتریس", "پرتو دوربرد", "گذر از موانع", "شبکه چندگردشی", "بهینه‌سازی مسیر", "ماتریس متراکم", "پرتو گسترده", "هزارتوی پیچیده", "مسیر استراتژیک", "شبکه با چگالی بالا", "مسیریابی استاد", "ناوبری بزرگ", "استاد مسیریابی",
        // Chapter 14 (209..224)
        "حد تنگاتنگ", "توان سخت‌گیرانه", "قید انرژی", "مصرف حداقل", "چالش انرژی", "باتری بحرانی", "دویدن با توان کم", "مدار بهینه", "صرفه‌جویی ماهرانه", "فشردگی انرژی", "فوق‌بهینه", "میکروانرژی", "ذخیره توان", "اوج حفاظت", "انرژی نهایی", "استاد نهایی انرژی",
        // Chapter 15 (225..240)
        "معمای کبیر", "مکانیک یکپارچه", "چالش طیفی", "پازل ماتریسی", "پیوند پیشرفته", "شبکه مدرن", "چالش استادی", "مسیریابی عمیق", "سمفونی اپتیک", "معمای کوانتومی", "پیوند نهایی", "استاد منشور", "سمفونی منطق", "ذهن برتر", "چالش بزرگ", "استاد پیشرفته",
        // Chapter 16 (241..256)
        "پرتو جنسیس", "نور آلفا", "آینه امگا", "پیدایش منشوری", "اوج منطق", "آرایه کوانتومی", "نور بی‌نهایت", "پیوند فرجامین", "همگرایی اپتیک", "طیف غایی", "ارگ لوما", "پرتو کیهانی", "کلید اعظم", "تاج لوما لاجیک", "طیف زنیت", "طیف نهایی (مرحله ۲۵۶)"
    )

    fun getLevelName(level: Level): String {
        if (!isPersian) return level.name
        val parts = level.levelId.split("_")
        val chapterNum = parts.getOrNull(1)?.toIntOrNull() ?: 1
        val levelNum = parts.getOrNull(3)?.toIntOrNull() ?: 1
        val globalIndex = (chapterNum - 1) * 16 + (levelNum - 1)

        val persianTitle = persianLevelNames.getOrElse(globalIndex) { level.name }
        return "فصل ${chapterNum.toString().toPersianDigits(true)} - مرحله ${levelNum.toString().toPersianDigits(true)}: $persianTitle"
    }

    fun getLevelDescription(level: Level): String {
        if (!isPersian) return level.description
        val parts = level.levelId.split("_")
        val chapterNum = parts.getOrNull(1)?.toIntOrNull() ?: 1
        val levelNum = parts.getOrNull(3)?.toIntOrNull() ?: 1
        val globalIndex = (chapterNum - 1) * 16 + (levelNum - 1)
        val persianTitle = persianLevelNames.getOrElse(globalIndex) { level.name }
        return "فصل $chapterNum: $persianTitle"
    }

    fun getCellTypeName(cellType: CellType): String {
        if (!isPersian) return cellType.name
        return when (cellType) {
            CellType.EMPTY -> "خالی"
            CellType.SOURCE -> "منبع نور"
            CellType.TARGET -> "هدف دریافت نور"
            CellType.MIRROR -> "آینه بازتابی"
            CellType.SPLITTER -> "منشور شکافنده"
            CellType.FILTER -> "فیلتر رنگی"
            CellType.BLOCK -> "مانع نوری"
            CellType.WIRE -> "سیم رسانا"
            CellType.GATE -> "دروازه منطقی"
        }
    }

    fun getLightColorName(color: LightColor): String {
        if (!isPersian) return color.name
        return when (color) {
            LightColor.WHITE -> "سفید"
            LightColor.RED -> "قرمز"
            LightColor.BLUE -> "آبی"
            LightColor.GREEN -> "سبز"
            LightColor.YELLOW -> "زرد"
        }
    }

    fun getGateTypeName(gateType: GateType): String {
        if (!isPersian) return gateType.name
        return when (gateType) {
            GateType.AND -> "AND (هم‌زمانی)"
            GateType.OR -> "OR (یا)"
            GateType.NOT -> "NOT (وارون‌ساز)"
        }
    }

    fun getValidationMessage(msg: String): String {
        if (!isPersian) return msg
        return when {
            msg.contains("Grid dimensions must be at least", ignoreCase = true) ->
                "ابعاد شبکه باید حداقل ۲×۲ باشد."
            msg.contains("Grid dimensions exceed maximum", ignoreCase = true) ->
                "ابعاد شبکه از حداکثر مجاز (۵۰×۵۰) فراتر رفته است."
            msg.contains("No Source exists", ignoreCase = true) ->
                "هیچ منبع نوری وجود ندارد. حداقل یک منبع نور لازم است."
            msg.contains("No Target exists", ignoreCase = true) ->
                "هیچ هدفی وجود ندارد. حداقل یک هدف دریافت نور لازم است."
            msg.contains("At least one required Target is needed", ignoreCase = true) ->
                "حداقل یک هدف اجباری برای اتمام مرحله لازم است."
            msg.contains("Duplicate component positions", ignoreCase = true) ->
                "چندین قطعه در یک مختصات مشترک قرار گرفته‌اند."
            msg.contains("is out of grid bounds", ignoreCase = true) ->
                "قطعه خارج از محدوده شبکه قرار دارد."
            msg.contains("has no accepted color configured", ignoreCase = true) ->
                "رنگ عبوری برای فیلتر مشخص نشده است."
            msg.contains("has no gate type specified", ignoreCase = true) ->
                "نوع دروازه منطقی مشخص نشده است."
            msg.contains("has no explicit color, defaulting to WHITE", ignoreCase = true) ->
                "رنگ منبع نور مشخص نشده، رنگ پیش‌فرض سفید اعمال شد."
            msg.contains("has no optical components", ignoreCase = true) ->
                "این مرحله هیچ قطعه اپتیکی ندارد و ممکن است غیرقابل حل باشد."
            msg.contains("Level name is empty", ignoreCase = true) ->
                "نام مرحله خالی است. انتخاب یک نام مناسب پیشنهاد می‌شود."
            else -> msg
        }
    }

    fun getAchievementTitle(achievement: Achievement): String {
        if (!isPersian) return achievement.title
        return when (achievement.id) {
            "FIRST_LIGHT" -> "نخستین درخشش"
            "TEN_LEVELS" -> "اپتیست تازه کار"
            "FIFTY_LEVELS" -> "دانشجوی طیف نور"
            "CENTURY" -> "استاد استادان نوری"
            "MASTER_256" -> "افسانه LumaLogic"
            "THREE_STAR" -> "درخشش بی‌نقص"
            "STAR_COLLECTOR" -> "کهکشان ستاره‌ها"
            "PERFECT_CHAPTER" -> "استاد فصل"
            "ENERGY_MASTER" -> "بهینه‌ساز انرژی"
            "LOGIC_MASTER" -> "معمار منطق"
            "NO_HINT" -> "هوش خالص"
            else -> achievement.title
        }
    }

    fun getAchievementDescription(achievement: Achievement): String {
        if (!isPersian) return achievement.description
        return when (achievement.id) {
            "FIRST_LIGHT" -> "اولین مرحله معمایی خود را حل کنید."
            "TEN_LEVELS" -> "۱۰ مرحله از بازی را با موفقیت تمام کنید."
            "FIFTY_LEVELS" -> "۵۰ مرحله مختلف را حل کنید."
            "CENTURY" -> "۱۰۰ مرحله از معماها را به اتمام برسانید."
            "MASTER_256" -> "تمام ۲۵۶ مرحله بازی را با موفقیت حل کنید!"
            "THREE_STAR" -> "در یک مرحله هر ۳ ستاره کامل را کسب کنید."
            "STAR_COLLECTOR" -> "مجموعاً ۱۰۰ ستاره در طول بازی جمع‌آوری کنید."
            "PERFECT_CHAPTER" -> "در تمام ۱۶ مرحله یک فصل ۳ ستاره بگیرید."
            "ENERGY_MASTER" -> "یک مرحله را با محدودیت انرژی کامل کنید."
            "LOGIC_MASTER" -> "یک مرحله حاوی دروازه منطقی را حل کنید."
            "NO_HINT" -> "۱۰ مرحله را بدون استفاده از راهنما حل کنید."
            else -> achievement.description
        }
    }
}

val LocalLocalization = staticCompositionLocalOf { LocalizationManager(false) }

@Composable
@ReadOnlyComposable
fun currentLocalization(): LocalizationManager {
    return LocalLocalization.current
}
