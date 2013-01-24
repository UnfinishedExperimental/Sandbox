/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox.g2d;

import java.nio.FloatBuffer;
import darwin.geometrie.data.*;
import darwin.util.math.base.vector.Vector3;
import darwin.util.math.composits.AABB;

import com.google.common.base.Optional;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class MeshUtil {

    public static AABB calcAABB(VertexBuffer vb) {        
        
        Optional<Element> elementByName = vb.layout.getElementByName("Position");
        if (elementByName.isPresent()) {
            Element e = elementByName.get();
            if (e.getVectorType().getElementCount() > 2) {
                Vector3 min = new Vector3(Float.MAX_VALUE);
                Vector3 max = new Vector3(Float.MIN_VALUE);
                
                for (Vertex v : vb) {
                    Number[] attribute = v.getAttribute(e);
                    
                    min.x = Math.min(min.x, attribute[0].floatValue());
                    max.x = Math.max(max.x, attribute[0].floatValue());
                    
                    min.y = Math.min(min.y, attribute[1].floatValue());
                    max.y = Math.max(max.y, attribute[1].floatValue());
                    
                    min.z = Math.min(min.z, attribute[2].floatValue());
                    max.z = Math.max(max.z, attribute[2].floatValue());
                }
                return AABB.fromMinMax(min, max);
            }
        }
        return null;
    }
}
