package engine.utils;

import java.util.ArrayList;
import java.util.List;

import engine.rendering.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * This class is used to load meshes data to Meshes Objects
 */
public class OBJLoader {

    /**
     * Load a 3D mesh, needs to be .obj
     * @param fileName the .obj path
     * @return the decoded Mesh
     * @throws Exception thrown if the fil couldn't be loaded or opened
     */
    public static Mesh loadMesh(String fileName) throws Exception {List<String> lines = Utils.readAllLines(fileName);
        
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Face> faces = new ArrayList<>();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    // Geometric vertex
                    Vector3f vec3f = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]));
                    vertices.add(vec3f);
                    break;
                case "vt":
                    // Texture coordinate
                    Vector2f vec2f = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]));
                    textures.add(vec2f);
                    break;
                case "vn":
                    // Vertex normal
                    Vector3f vec3fNorm = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]));
                    normals.add(vec3fNorm);
                    break;
                case "f":
                    Face face = new Face(tokens[1], tokens[2], tokens[3]);
                    faces.add(face);
                    break;
                default:
                    // Ignore other lines
                    break;
            }
        }
        return reorderLists(vertices, textures, normals, faces);
    }

    /**
     * Create the Mesh from the extracted lists of vertices, normals, texture coords and faces
     * @param posList the list of vertices
     * @param textCoordList the list of texture coords
     * @param normList the list of normals
     * @param facesList the list of face indices
     * @return a decoded Mesh usable by the Engine
     */
    private static Mesh reorderLists(List<Vector3f> posList, List<Vector2f> textCoordList, List<Vector3f> normList, List<Face> facesList) {

        List<Integer> indices = new ArrayList<>();
        // Create position array in the order it has been declared
        float[] posArr = new float[posList.size() * 3];
        int i = 0;
        for (Vector3f pos : posList) {
            posArr[i * 3] = pos.x;
            posArr[i * 3 + 1] = pos.y;
            posArr[i * 3 + 2] = pos.z;
            i++;
        }
        float[] textCoordArr = new float[posList.size() * 2];
        float[] normArr = new float[posList.size() * 3];

        for (Face face : facesList) {
            IdxGroup[] faceVertexIndices = face.getFaceVertexIndices();
            for (IdxGroup indValue : faceVertexIndices) {
                processFaceVertex(indValue, textCoordList, normList, indices, textCoordArr, normArr);
            }
        }
        int[] indicesArr;
        indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();
        Mesh mesh = new Mesh(posArr, textCoordArr, normArr, indicesArr);
        return mesh;
    }

    /**
     * Populate the face arrays, according to the extracted list
     * @param indices the indices of each type of data for the face (vertices, texture coords, normals)
     * @param textCoordList the list of texture coords
     * @param normList the list of normals
     * @param indicesList the list of face indices
     * @param texCoordArr the texture coords array to fill
     * @param normArr the normals array to fill
     */
    private static void processFaceVertex(IdxGroup indices, List<Vector2f> textCoordList, List<Vector3f> normList, List<Integer> indicesList, float[] texCoordArr, float[] normArr) {

        // Set index for vertex coordinates
        int posIndex = indices.idxPos;
        indicesList.add(posIndex);

        // Reorder texture coordinates
        if (indices.idxTextCoord >= 0) {
            Vector2f textCoord = textCoordList.get(indices.idxTextCoord);
            texCoordArr[posIndex * 2] = textCoord.x;
            texCoordArr[posIndex * 2 + 1] = 1 - textCoord.y;
        }
        if (indices.idxVecNormal >= 0) {
            // Reorder vectornormals
            Vector3f vecNorm = normList.get(indices.idxVecNormal);
            normArr[posIndex * 3] = vecNorm.x;
            normArr[posIndex * 3 + 1] = vecNorm.y;
            normArr[posIndex * 3 + 2] = vecNorm.z;
        }
    }

    /**
     * This class represent a face read from the .obj file
     */
    protected static class Face {

        /**
         * List of idxGroup groups for a face triangle (3 vertices per face).
         */
        private IdxGroup[] idxGroups;

        /**
         * Create a new Face
         * @param v1 vertices indices
         * @param v2 texture coords indices
         * @param v3 normals indices
         */
        public Face(String v1, String v2, String v3) {
            idxGroups = new IdxGroup[3];
            // Parse the lines
            idxGroups[0] = parseLine(v1);
            idxGroups[1] = parseLine(v2);
            idxGroups[2] = parseLine(v3);
        }

        /**
         * Create an IdxGroup from the String representation extractec from the .obj file
         * @param line the file to parse
         * @return an IdxGroup representing the face
         */
        private IdxGroup parseLine(String line) {
            IdxGroup idxGroup = new IdxGroup();

            String[] lineTokens = line.split("/");
            int length = lineTokens.length;
            idxGroup.idxPos = Integer.parseInt(lineTokens[0]) - 1;
            if (length > 1) {
                // It can be empty if the obj does not define text coords
                String textCoord = lineTokens[1];
                idxGroup.idxTextCoord = textCoord.length() > 0 ? Integer.parseInt(textCoord) - 1 : IdxGroup.NO_VALUE;
                if (length > 2) {
                    idxGroup.idxVecNormal = Integer.parseInt(lineTokens[2]) - 1;
                }
            }

            return idxGroup;
        }

        /**
         * Return the idxGroups of the face
         * @return an array of IdxGroups [vertices, texture coords, normals]
         */
        public IdxGroup[] getFaceVertexIndices() {
            return idxGroups;
        }
    }

    /**
     * This class represent a group of IDs used to compute a face from the .obj file
     */
    protected static class IdxGroup {

        public static final int NO_VALUE = -1;

        public int idxPos;

        public int idxTextCoord;

        public int idxVecNormal;

        /**
         * Initialize a new IdxGroup
         */
        public IdxGroup() {
            idxPos = NO_VALUE;
            idxTextCoord = NO_VALUE;
            idxVecNormal = NO_VALUE;
        }
    }
}
