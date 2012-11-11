/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox;

import org.lwjgl.util.mapped.MappedObject;
import org.lwjgl.util.mapped.MappedType;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
@MappedType(align=1)
public class DataBean extends MappedObject{

    public byte[] a;
}
