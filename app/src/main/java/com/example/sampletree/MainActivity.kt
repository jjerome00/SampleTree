package com.example.sampletree

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import com.caverock.androidsvg.RenderOptions
import com.caverock.androidsvg.SVG
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var svg: SVG
    private var newBM: Bitmap? = null
    private lateinit var bmCanvas: Canvas
    private var viewBox: RectF = RectF(0.0f, 0.0f, 0.0f, 0.0f)
    private var svgView: SVGView? = null

    private var greenCount = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = this.layout
        view.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    numberLabel.text = getGreenCountLabel()
                    svg = SVG.getFromAsset(assets, "tree_nocolor.svg")
                    measureSVGAndRender(svg)
                }
            }
        )

        levelDown.setOnClickListener {
            if (greenCount >= 0) {
                greenCount--
                numberLabel.text = getGreenCountLabel()
                renderSVG()
            }
        }

        levelUp.setOnClickListener {
            if (greenCount < 60) {
                greenCount++
                numberLabel.text = getGreenCountLabel()
                renderSVG()
            }
        }

    }

    private fun getGreenCountLabel(): String {
        return "${(greenCount + 1)} of 61"
    }

    private fun measureSVGAndRender(svg: SVG) {
        viewBox = svg.documentViewBox

        // svg's from inkscape might need this
        // http://bigbadaboom.github.io/androidsvg/faq.html#Dealing_with_Inkscape_files
        svg.setDocumentWidth("100%")
        svg.setDocumentHeight("100%")

        val height = (viewBox.bottom * this.map.width) / viewBox.right
        val width = this.map.width.toFloat()

        newBM = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        newBM?.let {
            bmCanvas = Canvas(it)
            bmCanvas.drawRGB(255, 255, 255)
            svgView = this.map
            renderSVG()
        }
    }

    private fun renderSVG() {
        val css = setLeafColors(greenCount)
        val renderOpts = RenderOptions.create().css(css)

        svg.renderToCanvas(bmCanvas, renderOpts)
        svgView?.setImageBitmap(newBM)
    }

    private fun setLeafColors(greenCount: Int): String {
        var result = ""

        for (i in 0..60) {
            if (i <= greenCount) {
                result += " #L$i .st1 { fill: rgb(87, 138, 0); } "
            } else {
                result += " #L$i .st1 { fill: #d3d3d3; } "
            }
        }

        return result
    }

}
