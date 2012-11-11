#version 120
#pragma include includes/StdLib.vert

in vec2 in_position;    //<Position>
out vec2 texcoord; 
//uniform mat4 mat_mvp;    //<MAT_MVP>

void main()
{
    texcoord = in_position;
    gl_Position = vec4(in_position, 0,1);
}