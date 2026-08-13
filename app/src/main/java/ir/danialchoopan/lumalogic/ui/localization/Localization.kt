package ir.danialchoopan.lumalogic.ui.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import ir.danialchoopan.lumalogic.data.model.Achievement
import ir.danialchoopan.lumalogic.data.model.Chapter
import ir.danialchoopan.lumalogic.data.model.Level

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

    fun getLevelName(level: Level): String {
        if (!isPersian) return level.name
        val parts = level.levelId.split("_")
        val chapterNum = parts.getOrNull(1)?.toIntOrNull() ?: 1
        val levelNum = parts.getOrNull(3)?.toIntOrNull() ?: 1

        val persianTitles = listOf(
            "نخستین درخشش", "مسیر مستقیم", "چرخش اول", "هدف دوگانه",
            "آینه متقاطع", "تقسیم پرتو", "فیلتر اولیه", "ترکیب طیف",
            "انرژی محدود", "دروازه منطق", "مسیر طولانی", "بازتاب متوالی",
            "شکافت نور", "شبکه رنگی", "حلقه بازتاب", "پایان مرحله"
        )
        val titleIdx = (levelNum - 1) % persianTitles.size
        return "فصل ${chapterNum.toString().toPersianDigits(true)} - مرحله ${levelNum.toString().toPersianDigits(true)}: ${persianTitles[titleIdx]}"
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
