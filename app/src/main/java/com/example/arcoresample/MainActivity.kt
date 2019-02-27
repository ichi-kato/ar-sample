package com.example.arcoresample

import android.graphics.Point
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.activity_main.*
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.ImageView
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.rendering.Renderable


class MainActivity : AppCompatActivity() {

    private lateinit var fragment: ArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment
        initializeGallery();

    }

    private fun initializeGallery() {

        val andy = ImageView(this)
        andy.setImageResource(R.drawable.droid_thumb)
        andy.contentDescription = "andy"
        andy.setOnClickListener { addObject(Uri.parse("andy.sfb")) }
        gallery_layout.addView(andy)

//    val cabin = ImageView(this)
//    cabin.setImageResource(R.drawable.cabin_thumb)
//    cabin.setContentDescription("cabin")
//    cabin.setOnClickListener({ view -> addObject(Uri.parse("Cabin.sfb")) })
//    gallery.addView(cabin)
//
//    val house = ImageView(this)
//    house.setImageResource(R.drawable.house_thumb)
//    house.setContentDescription("house")
//    house.setOnClickListener({ view -> addObject(Uri.parse("House.sfb")) })
//    gallery.addView(house)
//
//    val igloo = ImageView(this)
//    igloo.setImageResource(R.drawable.igloo_thumb)
//    igloo.setContentDescription("igloo")
//    igloo.setOnClickListener({ view -> addObject(Uri.parse("igloo.sfb")) })
//    gallery.addView(igloo)
    }

    private fun addObject(model: Uri) {
        val frame = fragment.arSceneView.arFrame
        val pt = getScreenCenter()
        if (frame != null) {
            val hits = frame.hitTest(pt.x.toFloat(), pt.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    placeObject(fragment, hit.createAnchor(), model)
                    break

                }
            }
        }
    }

    private fun placeObject(fragment: ArFragment, anchor: Anchor, model: Uri) {
        ModelRenderable.builder()
            .setSource(fragment.context!!, model)
            .build()
            .thenAccept { renderable -> addNodeToScene(fragment, anchor, renderable) }
            .exceptionally { throwable ->
                val builder = AlertDialog.Builder(this)
                builder.setMessage(throwable.message)
                    .setTitle("Codelab error!")
                val dialog = builder.create()
                dialog.show()
                null
            }
    }

    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, renderable: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(fragment.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        node.select()
    }

    private fun getScreenCenter(): Point {
        val vw = findViewById<View>(android.R.id.content)
        return Point((vw.width / 2), vw.height / 2)
    }
}


