#version 120
#pragma include includes/StdLib.vert

in vec3 in_position;    //<Position>
in vec3 in_normal;    //<Normal>

out vec3 normal; 
out vec3 eye;
out float zlerp;

uniform mat4 view; //<MAT_V>
uniform mat4 projection; //<MAT_P>

//uniform mat2x4 dual;
uniform vec4 dual1;
uniform vec4 dual2;

void main()
{
    vec3 position = in_position;
    position.y -=1.5;

    zlerp = position.y + 7;
    zlerp = min(1, max(0,zlerp / (12)));
zlerp = pow(zlerp , 2);

    vec4 d1 = dual1;
    d1.x *= zlerp;
    vec4 d2 = dual2;

    float len = length(d1);
    d1 /= len;
    d2 /= len;

    position = position + 2.0 * cross(d1.yzw, cross(d1.yzw, position) + d1.x*position);

    vec3 trans = 2.0*(d1.x*d2.yzw - d2.x*d1.yzw + cross(d1.yzw, d2.yzw));
    position += trans;

    normal = in_normal + 2.0*cross(d1.yzw, cross(d1.yzw, in_normal) + d1.x*in_normal);

    vec4 p = view * vec4(position,1);
    eye = -p.xyz;

    gl_Position = projection * view * p;
}