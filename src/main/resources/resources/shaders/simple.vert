#version 120
#pragma include includes/StdLib.vert

in vec2 in_position;    //<Position>
out vec2 texcoord; 

void main()
{
    texcoord = in_position;
    gl_Position = vec4(in_position*2-1., 0,1);
}