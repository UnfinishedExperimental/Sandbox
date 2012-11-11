#version 120
#pragma include includes/StdLib.frag

in vec2 texcoord; 

uniform vec3 lightPos = vec3(0,0,1);
uniform sampler2D data;

void main()
{
    vec2 pos = texcoord * 2. - 1.;
    FragColor = vec4(pos*0.5+0.5,0,1);
}
