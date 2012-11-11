#version 120
#pragma include includes/StdLib.frag

in vec2 texcoord; 

uniform sampler2D data;

void main()
{
    vec2 pos = texcoord * 2. - 1.;
    FragColor = texture(data, texcoord)+vec4(0,0,0.0,0);
}
