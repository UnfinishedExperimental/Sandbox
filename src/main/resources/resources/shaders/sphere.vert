#version 120
#pragma include includes/StdLib.vert

const vec3 lightPos = vec3(0,30,10);

in vec3 in_position;    //<Position>
in vec3 in_normal;    //<Normal>

out vec3 normal;
out vec3 eye;
out vec3 lightDir;
out vec2 uv;

uniform mat4 v; //<MAT_V>
uniform mat4 mv; //<MAT_MV>
uniform mat3 nv; //<MAT_NV>
uniform mat4 mvp; //<MAT_MVP>

#define PI 3.14159265
#define Grad2Rad(x) (x * PI / 180)
const vec2 FOV = vec2(Grad2Rad(65 * 0.5), Grad2Rad(48 * 0.5));

void main()
{
    vec3 o = in_position;
    o.z = -o.z;

    vec4 p = mv * vec4(o, 1);

    lightDir = (v * vec4(lightPos, 1)).xyz - p.xyz;

    normal = in_normal;
    normal.z = -normal.z;
    normal = nv * normal;
 
    eye = -p.xyz;
    gl_Position =  mvp * p;

    float sinX = normalize(o.xz).x;
    float sinY = normalize(o.yz).x;
    
    vec2 sinXY = vec2(sinX, sinY);    

    uv = (asin(sinXY) / FOV)*0.5+0.5;
}
