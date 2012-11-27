#version 120
#pragma include includes/StdLib.vert

in vec3 in_position;    //<Position>
uniform mat4 view; //<MAT_V>
uniform mat4 projection; //<MAT_P>

const float PI = 3.14159265;

void main()
{
    vec3 p = in_position;
    //float theta = acos(p.z+1);
    float phi = atan(abs(p.y/p.x));
    float phi2 = atan(abs(p.z/p.x));
    p *= sin(phi*5)*0.1+1;
    p *= sin(phi2*10)*0.1+1;

    gl_Position =  projection * view * vec4(p*1.5, 1.);

}