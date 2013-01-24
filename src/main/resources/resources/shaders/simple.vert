#version 120
#pragma include includes/StdLib.vert

in vec3 in_position;    //<Position>
in vec3 in_normal;    //<Normal>

out vec3 normal; 
out vec3 eye;

uniform mat4 view; //<MAT_V>
uniform mat4 projection; //<MAT_P>

//uniform mat2x4 dual;
uniform vec4 dual1;
uniform vec4 dual2;

void main()
{
    vec3 position = in_position;
    //position.y -=2;
    float scale = 1.8;
    position.xyz *= scale;

    float zlerp = position.y - (-5*scale);
    zlerp = min(1, max(0,zlerp / (11*scale)));

    vec4 d1 = dual1 * zlerp;
    vec4 d2 = dual2;

    float len = length(dual1);
    d1 /= len;
    d2 /= len;

    position = position + 2.0 * cross(d1.yzw, cross(d1.yzw, position) + d1.x*position);
    vec3 trans = 2.0*(d1.x*d2.yzw - d2.x*d1.yzw + cross(d1.yzw, d2.yzw));
    position += trans;

    normal = in_normal + 2.0*cross(d1.yzw, cross(d1.yzw, in_normal) + d1.x*in_normal);

    vec4 p = view * vec4(position,1);
    eye = -p.xyz;
    gl_Position = projection * p;
}