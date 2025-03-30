local function read_bin_file(path)
    local file, err = io.open(path, "rb")
    if not file then
        error("Unable to open request bin file: ", err)
    end
    local content = file:read("*a")
    file:close()
    return content
end

local payload = read_bin_file("./corneast-core/request/reduce.bin")

wrk.method = "POST"
wrk.headers["Content-Type"] = "application/x-protobuf"

request = function()
    return wrk.format(nil, nil, nil, payload)
end

