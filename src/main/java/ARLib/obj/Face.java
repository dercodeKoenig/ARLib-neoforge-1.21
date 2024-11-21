package ARLib.obj;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class Face
{
    public Vertex[] vertices;
    public Vertex faceNormal;
    public Vertex[] original_vertices;
    public Vertex original_faceNormal;
    public TextureCoordinate[] textureCoordinates;

    public void addFaceForRender(PoseStack stack, VertexConsumer v, int packedLight, int packedOverlay)
    {
        if (faceNormal == null)
        {
            faceNormal = this.calculateFaceNormal();
        }

        //tessellator.setNormal(faceNormal.x, faceNormal.y, faceNormal.z);

        float averageU = 0F;
        float averageV = 0F;

        if ((textureCoordinates != null) && (textureCoordinates.length > 0))
        {
            for (TextureCoordinate textureCoordinate : textureCoordinates) {
                averageU += textureCoordinate.u;
                averageV += textureCoordinate.v;
            }

            averageU = averageU / textureCoordinates.length;
            averageV = averageV / textureCoordinates.length;
        }


        for (int i = 0; i < vertices.length; ++i)
        {

            if ((textureCoordinates != null) && (textureCoordinates.length > 0))
            {
                v.addVertex(stack.last(),vertices[i].x, vertices[i].y, vertices[i].z)
                        .setNormal(faceNormal.x, faceNormal.y, faceNormal.z)
                        .setColor(0xFFFFFFFF)
                        .setLight(packedLight)
                        .setOverlay(packedOverlay)
                        .setUv(textureCoordinates[i].u, textureCoordinates[i].v);
            }
            else
            {
            }
        }
    }

    public void rotateAroundPoint(Vector3f point, Vector3f axis, float angleDegrees) {
        Quaternionf rotation = new Quaternionf().fromAxisAngleDeg(axis.normalize(), angleDegrees);

        if (original_vertices == null) {
            original_vertices = new Vertex[vertices.length];
            for (int i = 0; i < vertices.length; i++) {
                original_vertices[i] = new Vertex(vertices[i].x, vertices[i].y, vertices[i].z);
            }
        }

        if (original_faceNormal == null) {
            original_faceNormal = new Vertex(faceNormal.x, faceNormal.y, faceNormal.z);
        }


        // Rotate vertices
        for (int i = 0; i < original_vertices.length; i++) {
            Vector3f vertexPos = new Vector3f(original_vertices[i].x, original_vertices[i].y, original_vertices[i].z);
            vertexPos.sub(point).rotate(rotation).add(point);
            vertices[i].x = vertexPos.x;
            vertices[i].y = vertexPos.y;
            vertices[i].z = vertexPos.z;
        }

        Vector3f normalVec = new Vector3f(original_faceNormal.x, original_faceNormal.y, original_faceNormal.z);
        normalVec.rotate(rotation);
        faceNormal = new Vertex(normalVec.x, normalVec.y, normalVec.z);

    }


    public Vertex calculateFaceNormal() {
        // Create two vectors from the triangle's vertices
        Vec3 v1 = new Vec3(vertices[1].x - vertices[0].x, vertices[1].y - vertices[0].y, vertices[1].z - vertices[0].z);
        Vec3 v2 = new Vec3(vertices[2].x - vertices[0].x, vertices[2].y - vertices[0].y, vertices[2].z - vertices[0].z);

        // Compute the cross product manually
        double nx = v1.y * v2.z - v1.z * v2.y;
        double ny = v1.z * v2.x - v1.x * v2.z;
        double nz = v1.x * v2.y - v1.y * v2.x;

        // Create the normal vector
        Vec3 normalVector = new Vec3(nx, ny, nz);

        // Normalize the normal vector
        normalVector = normalVector.normalize();

        // Return a new Vertex with the normal values
        return new Vertex((float) normalVector.x, (float) normalVector.y, (float) normalVector.z);
    }

}