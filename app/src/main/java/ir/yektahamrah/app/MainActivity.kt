package ir.yektahamrah.app

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.widget.*
import kotlin.math.max

class MainActivity : Activity() {

    private lateinit var root: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHome()
    }

    private fun base(): LinearLayout {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(245,247,250))
            setPadding(28, 24, 28, 24)
        }
        return root
    }

    private fun title(text: String, size: Float = 24f): TextView =
        TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(Color.rgb(16,24,40))
            setPadding(0, 10, 0, 10)
            gravity = android.view.Gravity.RIGHT
        }

    private fun button(text: String, action: () -> Unit): Button =
        Button(this).apply {
            this.text = text
            textSize = 15f
            isAllCaps = false
            setOnClickListener { action() }
            layoutParams = LinearLayout.LayoutParams(-1, 58).apply {
                setMargins(0, 8, 0, 8)
            }
        }

    private fun showHome() {
        base()
        root.addView(title("Yekta Hamrah", 30f))
        root.addView(title("تشخیص و تست سلامت گوشی", 16f))

        val info = deviceInfo()
        val card = TextView(this).apply {
            text = "📱 ${info.first}\\n${info.second}"
            textSize = 16f
            setTextColor(Color.DKGRAY)
            setBackgroundColor(Color.WHITE)
            setPadding(20,20,20,20)
            gravity = android.view.Gravity.RIGHT
        }
        root.addView(card, LinearLayout.LayoutParams(-1, 120))

        root.addView(button("🔍 شروع تست کامل گوشی") { showTests() })
        root.addView(button("👆 تست تخصصی تاچ و Ghost Touch") { showTouchTest() })
        root.addView(button("⚙️ مشخصات سخت‌افزار") { showHardware() })
        root.addView(button("📄 گزارش تست") { showReport() })

        setContentView(root)
    }

    private fun showTests() {
        base()
        root.addView(title("تست‌های دستگاه"))
        root.addView(title("هر تست را جداگانه اجرا کنید. نتیجه‌ها در نسخه بعدی در گزارش جمع می‌شوند.", 14f))
        root.addView(button("👆 صفحه لمسی") { showTouchTest() })
        root.addView(button("🎨 نمایشگر و رنگ‌ها") { showDisplayTest() })
        root.addView(button("🔊 اسپیکر") { showSpeakerTest() })
        root.addView(button("🎤 میکروفون") { toast("تست میکروفون در نسخه 1.1 اضافه می‌شود.") })
        root.addView(button("📳 ویبره") { showVibrateTest() })
        root.addView(button("🧭 سنسورها") { showSensors() })
        root.addView(button("🔋 باتری") { showBattery() })
        root.addView(button("↩ بازگشت") { showHome() })
        setContentView(root)
    }

    private fun showTouchTest() {
        val layout = FrameLayout(this)
        val testView = TouchDiagnosticView(this)
        layout.addView(testView, FrameLayout.LayoutParams(-1,-1))

        val back = Button(this).apply {
            text = "× خروج"
            isAllCaps = false
            setOnClickListener { showHome() }
        }
        val lp = FrameLayout.LayoutParams(150,60)
        lp.gravity = android.view.Gravity.TOP or android.view.Gravity.START
        lp.setMargins(12,12,0,0)
        layout.addView(back, lp)

        val result = TextView(this).apply {
            text = "صفحه را لمس و با انگشت روی آن حرکت دهید. لمس ناخواسته نیز ثبت می‌شود."
            textSize = 14f
            setTextColor(Color.WHITE)
            setBackgroundColor(0x99000000.toInt())
            setPadding(18,12,18,12)
            gravity = android.view.Gravity.RIGHT
        }
        val rp = FrameLayout.LayoutParams(-1,100)
        rp.gravity = android.view.Gravity.BOTTOM
        layout.addView(result,rp)

        testView.onStats = { touches, suspicious ->
            result.text = "لمس ثبت‌شده: $touches    |    موارد مشکوک: $suspicious"
        }
        setContentView(layout)
    }

    private fun showDisplayTest() {
        val v = DisplayTestView(this)
        v.setOnClickListener { showHome() }
        setContentView(v)
    }

    private fun showSpeakerTest() {
        base()
        root.addView(title("تست اسپیکر"))
        root.addView(title("برای تست، دکمه پخش را بزنید و صدای خروجی را بررسی کنید.", 16f))
        root.addView(button("🔊 پخش صدای تست") {
            val tone = android.media.ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 90)
            tone.startTone(android.media.ToneGenerator.TONE_PROP_BEEP, 800)
        })
        root.addView(button("↩ بازگشت") { showTests() })
        setContentView(root)
    }

    private fun showVibrateTest() {
        base()
        root.addView(title("تست ویبره"))
        root.addView(button("📳 لرزش") {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
            if (Build.VERSION.SDK_INT >= 26)
                vibrator.vibrate(android.os.VibrationEffect.createOneShot(600, 180))
            else @Suppress("DEPRECATION") vibrator.vibrate(600)
        })
        root.addView(button("↩ بازگشت") { showTests() })
        setContentView(root)
    }

    private fun showSensors() {
        base()
        root.addView(title("سنسورها"))
        val sm = getSystemService(Context.SENSOR_SERVICE) as android.hardware.SensorManager
        val list = sm.getSensorList(android.hardware.Sensor.TYPE_ALL)
        val names = list.map { "• ${it.name}" }.distinct()
        root.addView(TextView(this).apply {
            text = if (names.isEmpty()) "سنسوری شناسایی نشد." else names.joinToString("\\n")
            textSize = 15f
            setPadding(0,10,0,10)
        })
        root.addView(button("↩ بازگشت") { showTests() })
        setContentView(root)
    }

    private fun showBattery() {
        base()
        root.addView(title("وضعیت باتری"))
        val bm = getSystemService(Context.BATTERY_SERVICE) as android.os.BatteryManager
        val percent = bm.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY)
        root.addView(title("شارژ فعلی: $percent%", 20f))
        root.addView(title("توجه: سلامت واقعی باتری در همه گوشی‌ها از طریق API استاندارد قابل تشخیص نیست.", 14f))
        root.addView(button("↩ بازگشت") { showTests() })
        setContentView(root)
    }

    private fun showHardware() {
        base()
        root.addView(title("مشخصات سخت‌افزار"))
        val text = TextView(this).apply {
            textSize = 15f
            text = deviceInfoFull()
            setPadding(0,10,0,10)
        }
        root.addView(text)
        root.addView(button("↩ بازگشت") { showHome() })
        setContentView(root)
    }

    private fun showReport() {
        base()
        root.addView(title("گزارش سلامت دستگاه"))
        root.addView(title("نسخه اولیه پروژه", 18f))
        root.addView(title("🟢 اطلاعات دستگاه: قابل دریافت\\n🟡 تست‌های پایه: آماده\\n🟡 تست Ghost Touch: آماده\\n⚪ PDF: در مرحله بعدی\\n\\nگزارش حرفه‌ای PDF و امتیاز سلامت در نسخه بعدی اضافه می‌شود.", 16f))
        root.addView(button("↩ بازگشت") { showHome() })
        setContentView(root)
    }

    private fun deviceInfo(): Pair<String,String> {
        return Pair("${Build.MANUFACTURER} ${Build.MODEL}", "Android ${Build.VERSION.RELEASE} • API ${Build.VERSION.SDK_INT}")
    }

    private fun deviceInfoFull(): String {
        return """
            سازنده: ${Build.MANUFACTURER}
            مدل: ${Build.MODEL}
            برند: ${Build.BRAND}
            دستگاه: ${Build.DEVICE}
            Android: ${Build.VERSION.RELEASE}
            API Level: ${Build.VERSION.SDK_INT}
            برد: ${Build.BOARD}
            محصول: ${Build.PRODUCT}
            سخت‌افزار: ${Build.HARDWARE}
        """.trimIndent()
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

    class TouchDiagnosticView(context: Context) : View(context) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val path = Path()
        private var touches = 0
        private var suspicious = 0
        private var lastEventTime = 0L
        private var lastX = -1f
        private var lastY = -1f
        var onStats: ((Int,Int)->Unit)? = null

        init {
            setBackgroundColor(Color.BLACK)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 5f
            paint.color = Color.rgb(50, 220, 130)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val w = width.toFloat()
            val h = height.toFloat()
            paint.color = 0x553A86FF
            paint.strokeWidth = 1f
            for (i in 1..9) canvas.drawLine(w*i/10,0f,w*i/10,h,paint)
            for (i in 1..15) canvas.drawLine(0f,h*i/16,w,h*i/16,paint)
            paint.color = Color.WHITE
            paint.textSize = 34f
            canvas.drawText("TOUCH TEST", 24f, 70f, paint)
            paint.textSize = 18f
            canvas.drawText("صفحه را کامل لمس کنید", 24f, 105f, paint)
            paint.style = Paint.Style.STROKE
            paint.color = Color.rgb(50,220,130)
            canvas.drawPath(path,paint)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            val now = System.currentTimeMillis()
            when(event.actionMasked) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                    touches++
                    val x = event.getX(event.actionIndex)
                    val y = event.getY(event.actionIndex)
                    // A new touch arriving extremely close to a previous touch without
                    // the user moving through the screen is flagged as suspicious.
                    if (lastEventTime > 0 && now-lastEventTime < 70 &&
                        kotlin.math.abs(x-lastX) < 8 && kotlin.math.abs(y-lastY) < 8) suspicious++
                    lastX=x; lastY=y; lastEventTime=now
                    path.moveTo(x,y)
                    invalidate()
                    onStats?.invoke(touches,suspicious)
                }
                MotionEvent.ACTION_MOVE -> {
                    for(i in 0 until event.pointerCount) path.lineTo(event.getX(i),event.getY(i))
                    invalidate()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> invalidate()
            }
            return true
        }
    }

    class DisplayTestView(context: Context) : View(context) {
        private var mode = 0
        private val colors = intArrayOf(Color.WHITE, Color.BLACK, Color.RED, Color.GREEN, Color.BLUE, Color.GRAY)
        override fun onDraw(c: Canvas) {
            c.drawColor(colors[mode])
            val p = Paint(Paint.ANTI_ALIAS_FLAG)
            p.color = if(mode==0 || mode==2 || mode==4) Color.BLACK else Color.WHITE
            p.textSize = 34f
            c.drawText("برای تغییر رنگ لمس کنید", 40f, height/2f, p)
            p.textSize = 16f
            c.drawText("خروج: دکمه Back", 40f, height/2f+40, p)
        }
        override fun onTouchEvent(e: MotionEvent): Boolean {
            if(e.action == MotionEvent.ACTION_UP) { mode=(mode+1)%colors.size; invalidate() }
            return true
        }
    }
}
