/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox.meshstuff;

import darwin.geometrie.data.*;
import darwin.geometrie.unpacked.Mesh;
import darwin.util.math.base.vector.Vector3;
import darwin.util.math.composits.AABB;

import com.google.common.base.Optional;

import static darwin.geometrie.io.ModelReader.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class MeshUtil {

    public boolean hasNormals(VertexBuffer vb) {
        return vb.layout.getElementByName(NORMAL_ATTRIBUTE).isPresent();
    }

    public static void switchOrdering(Mesh m) {
        
        int[] i = m.getIndicies();
        for (int j = 0; j < i.length; j += 3) {
            int t = i[j];
            i[j] = i[j + 2];
            i[j + 2] = t;
        }
    }

    public static AABB calcAABB(VertexBuffer vb) {
        Optional<Element> elementByName = vb.layout.getElementByName(POSITION_ATTRIBUTE);
        if (elementByName.isPresent()) {
            return calcAABB(vb, elementByName.get());
        }
        throw new IllegalArgumentException("the provided vertex buffer has no element with the name \"Position\"");
    }

    public static AABB calcAABB(VertexBuffer vb, Element element) {
        if (vb.layout.hasElement(element)
            && element.getVectorType().getElementCount() > 2) {
            Vector3 min = new Vector3(Float.MAX_VALUE);
            Vector3 max = new Vector3(Float.MIN_VALUE);

            for (Vertex v : vb) {
                Number[] attribute = v.getAttribute(element);

                min.x = Math.min(min.x, attribute[0].floatValue());
                max.x = Math.max(max.x, attribute[0].floatValue());

                min.y = Math.min(min.y, attribute[1].floatValue());
                max.y = Math.max(max.y, attribute[1].floatValue());

                min.z = Math.min(min.z, attribute[2].floatValue());
                max.z = Math.max(max.z, attribute[2].floatValue());
            }
            return AABB.fromMinMax(min, max);
        } else {
            throw new IllegalArgumentException("The provided element must be present "
                                               + "in the provided vertex buffer and has to have at least 3 values");
        }
    }
}
