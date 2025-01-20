# JSON to Apollo Connector

A simple tool that generates either
* the `GraphQL` types derived from a set of `JSON` paylods, 
* an Apollo Connector `selection` set, or
* a base Apollo Connector template, with directives and a sample query operation

for a given collection of `JSON` files located in a folder.

## Usage:
```
Usage: JsonToGQL [-chstV] [-o=output] <file|folder>
Converts a JSON payload (or a collection of JSON payloads) to an Apollo
Connector spec.  When passing a folder, it is expected that these are from the
same collection - for instance, a collection of articles, or blog posts - as
the tool will attempt to merge these to derive a single connector schema.

      <file|folder>          A single JSON file or a folder with a collection
                               of JSON files.
  -c                         output an Apollo Connector template
  -h, --help                 Show this help message and exit.
  -o, --output-file=output   where to write the output
  -s                         [default] output the Apollo Connector selection
  -t                         output the GraphQL schema types
  -V, --version              Print version information and exit.
```

## A `file` or a `folder`?

If you pass a `JSON file` as the source the tool will attempt to derive the types and the Apollo connector 
selection from it. However, one single file might not be representative of all the possible variations of the output -
which is why the tool will also accept a `folder` as an argument. In essence, the more responses we have, the better 
the resulting GraphQL schema will be. See the examples below.

## Examples

### 1. Single JSON input
If we have the following JSON Input saved in `./user/preferences/50.json`:
```json
{
  "userId": 50,
  "favouriteTeams": ["Luton"],
  "favouriteLeagues": [
    "premier-league",
    "championship",
    "scottish-premiership"
  ],
  "joiningDate": "2023-12-11"
}

```

then we can run `java -jar JsonToGQL.jar ./user/preferences/50.json` and the output will be:
```graphql
extend schema
  @link(url: "https://specs.apollo.dev/federation/v2.10", import: ["@key"])
  @link(
    url: "https://specs.apollo.dev/connect/v0.1"
    import: ["@connect", "@source"]
  )
  @source(name: "api", http: { baseURL: "http://localhost:4010" })

type Root {
  userId: Int
  favouriteTeams: [String]
  favouriteLeagues: [String]
  joiningDate: String
}

type Query {
  root: Root
    @connect(
      source: "api"
      http: { GET: "/test" }
      selection: """
      userId
      favouriteTeams
      favouriteLeagues
      joiningDate
      """
    )
}
```

### 2. Merging a collection of JSON input files 

Let's say we have the above payload split in 3 `JSON` files:

`a.json`:
```json
{
  "userId": 50
}
```

`b.json`:
```json
{
  "favouriteTeams": ["Luton"],
  "joiningDate": "2023-12-11"
}
```

`c.json`:
```json
{
  "favouriteLeagues": [
    "premier-league",
    "championship",
    "scottish-premiership"
  ]
}
```

and that all files are located in the `./merge` folder. By running `java -jar JsonToGQL.jar ./merge` the tool will
output the (same) schema as above:

```graphql
extend schema
@link(url: "https://specs.apollo.dev/federation/v2.10", import: ["@key"])
@link(
    url: "https://specs.apollo.dev/connect/v0.1"
    import: ["@connect", "@source"]
)
@source(name: "api", http: { baseURL: "http://localhost:4010" })

type Root {
    userId: Int
    favouriteLeagues: [String]
    favouriteTeams: [String]
    joiningDate: String
}

type Query {
    root: Root
    @connect(
        source: "api"
        http: { GET: "/test" }
        selection: """
        userId
        favouriteLeagues
        favouriteTeams
        joiningDate
        """
    )
}
```

which is the same output as before.