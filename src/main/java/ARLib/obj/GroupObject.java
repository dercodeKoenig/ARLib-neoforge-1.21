package ARLib.obj;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.ArrayList;

import static net.minecraft.client.renderer.RenderStateShard.*;

public class GroupObject
{
    public String name;
    public ArrayList<Face> faces = new ArrayList<>();
    public VertexFormat.Mode drawMode;
    public VertexFormat vertexMode;


    public static VertexFormat POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL =            VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("Color", VertexFormatElement.COLOR)
            .add("UV0", VertexFormatElement.UV0)
            .add("UV1", VertexFormatElement.UV1)
            .add("UV2", VertexFormatElement.UV2)
            .add("Normal", VertexFormatElement.NORMAL)
            .build();
    public static VertexFormat POSITION_COLOR_OVERLAY_LIGHT_NORMAL =            VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("Color", VertexFormatElement.COLOR)
            .add("UV1", VertexFormatElement.UV1)
            .add("UV2", VertexFormatElement.UV2)
            .add("Normal", VertexFormatElement.NORMAL)
            .build();

    public GroupObject()
    {
        this("");
    }

    public GroupObject(String name)
    {
        this(name, VertexFormat.Mode.DEBUG_LINES);
    }

    public GroupObject(String name, VertexFormat.Mode drawingMode)
    {
        this.name = name;
        this.drawMode = drawingMode;
    }


    public void render(PoseStack stack,  MultiBufferSource bufferSource, ResourceLocation texture, int packedLight, int packedOverlay)
    {

        RenderType r =  RenderType.create("fuckYouForge",
                POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                drawMode,
                1536,
                true,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                        .setOverlayState(OVERLAY)
                        .setLightmapState(LIGHTMAP)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setTextureState(new TextureStateShard(texture,false,false))
                        .createCompositeState(false)
        );

        VertexConsumer v =  bufferSource.getBuffer(r);
        if (faces.size() > 0)
        {
            for (Face face : faces)
            {
                face.addFaceForRender(stack,v, packedLight,packedOverlay);
            }
        }
    }
}