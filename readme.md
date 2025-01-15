# JSON to Apollo Connector

A simple tool that generates either
* the `GraphQL` types derived from a set of `JSON` paylods, 
* an Apollo Connector `selection` set, or
* a base Apollo Connector template, with directives and a sample query operation

for a given collection of `JSON` files located in a folder.

## Usage:
```
Usage: walk [-chstV] [-o=output] <folder>
Walks a folder containing JSON file and derives an Apollo Connector GQL schema
      <folder>               The folder where JSON payloads exist.
  -c                         output an Apollo Connector template
  -h, --help                 Show this help message and exit.
  -o, --output-file=output   where to write the output
  -s                         [default] output the Apollo Connector selection
  -t                         output the GraphQL schema types
  -V, --version              Print version information and exit.
```

## Why is the input a `folder` and not a `file`?

Simply because one JSON payload might not be representative of all the possible results of an API invocation. 
If an OAS/Swagger spec is not available then we derive the schema from JSON responses, so the more responses 
we have, the better the resulting GraphQL schema will be.

## Examples

### 1. Single JSON input
JSON Input:
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

GraphQL Types generated from `java -jar JsonToGQL.jar ./user -o ./connector-spec.graphql`:

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

### 2. Merge JSON
Let's say we have the above payload split in 3:

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

All located in the `./merge` folder. 
Running `java -jar JsonToGQL.jar ./merge -o ./connector-spec.graphql` yields:

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