#version 120
#pragma include includes/StdLib.vert

in vec2 lookup; //<lookup>
uniform sampler2D data;

out float life;

void main()
{
    vec4 data = texture(data, lookup);

    vec3 pos = data.xyz;
    life = data.a;

    gl_Position = vec4(data.xy*2-1, 0, 1.);
}