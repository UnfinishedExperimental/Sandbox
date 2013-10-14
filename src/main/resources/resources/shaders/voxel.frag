#version 120
#pragma include includes/StdLib.frag

in vec3 color;

void main()
{
    FragColor = vec4(color,0);
}