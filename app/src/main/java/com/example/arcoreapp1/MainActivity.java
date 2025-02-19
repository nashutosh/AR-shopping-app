package com.example.arcoreapp1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {

    private ArFragment fragment;

    private Uri selectedObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);

        InitializeGallery();

        fragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if(plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                        return;
                    }
                    Anchor anchor = hitResult.createAnchor();

                    placeObject(fragment, anchor, selectedObject);
                }
        );
    }

    private void InitializeGallery() {
        LinearLayout gallery = findViewById(R.id.gallery_layout);

        ImageView chair = new ImageView(this);
        chair.setImageResource(R.drawable.chair_thumb);
        chair.setContentDescription("chair");
        chair.setOnClickListener(view -> {selectedObject = Uri.parse("chair.sfb");});
        gallery.addView(chair);

        ImageView couch = new ImageView(this);
        couch.setImageResource(R.drawable.couch_thumb);
        couch.setContentDescription("couch");
        couch.setOnClickListener(view -> {selectedObject = Uri.parse("couch.sfb");});
        gallery.addView(couch);

        ImageView lamp = new ImageView(this);
        lamp.setImageResource(R.drawable.lamp_thumb);
        lamp.setContentDescription("lamp");
        lamp.setOnClickListener(view -> {selectedObject = Uri.parse("lamp.sfb");});
        gallery.addView(lamp);

//        ImageView milleniumfalcon = new ImageView(this);
//        milleniumfalcon.setImageResource(R.drawable.milleniumfalcon_thumb);
//        milleniumfalcon.setContentDescription("milleniumfalcon");
//        milleniumfalcon.setOnClickListener(view -> {selectedObject = Uri.parse("milleniumfalcon.sfb");});
//        gallery.addView(milleniumfalcon);
//
//        ImageView stardestroyer = new ImageView(this);
//        stardestroyer.setImageResource(R.drawable.stardestroyer_thumb);
//        stardestroyer.setContentDescription("stardestroyer");
//        stardestroyer.setOnClickListener(view -> {selectedObject = Uri.parse("stardestroyer.sfb");});
//        gallery.addView(stardestroyer);
//
//        ImageView tiefighter = new ImageView(this);
//        tiefighter.setImageResource(R.drawable.tiefighter_thumb);
//        tiefighter.setContentDescription("tiefighter");
//        tiefighter.setOnClickListener(view -> {selectedObject = Uri.parse("tiefighter.sfb");});
//        gallery.addView(tiefighter);
//
//        ImageView xwingstarfighter = new ImageView(this);
//        xwingstarfighter.setImageResource(R.drawable.xwingstarfighter_thumb);
//        xwingstarfighter.setContentDescription("xwingstarfighter");
//        xwingstarfighter.setOnClickListener(view -> {selectedObject = Uri.parse("xwingstarfighter.sfb");});
//        gallery.addView(xwingstarfighter);
    }

    private void placeObject(ArFragment fragment, Anchor anchor, Uri model) {
        ModelRenderable.builder()
                .setSource(fragment.getContext(), model)
                .build()
                .thenAccept(renderable -> addNodeToScene(fragment, anchor, renderable))
                .exceptionally((throwable -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(throwable.getMessage())
                            .setTitle("Error!");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return null;
                }));
    }

    private void addNodeToScene(ArFragment fragment, Anchor anchor, Renderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(fragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        fragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }
}
