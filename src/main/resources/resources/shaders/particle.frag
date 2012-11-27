#version 120
#pragma include includes/StdLib.frag

in float life;

void main()
{
    FragColor = vec4(1,1,life,0);
}